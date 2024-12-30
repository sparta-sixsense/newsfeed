package com.sixsense.newsfeed.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sixsense.newsfeed.domain.Post;
import com.sixsense.newsfeed.domain.QUser;
import com.sixsense.newsfeed.domain.User;
import com.sixsense.newsfeed.dto.SignUpResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sixsense.newsfeed.domain.QPost.post;
import static com.sixsense.newsfeed.domain.QUser.user;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager entityManager;

    JPAQueryFactory queryFactory;

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void beforeEach() {
        queryFactory = new JPAQueryFactory(entityManager);

        User user1 = User.builder()
                .name("user1")
                .email("user1@gmail.com")
                .password("123")
                .profileImgUrl("https://~~")
                .age(20)
                .address("사랑시 고백구 행복동")
                .build();

        User user2 = User.builder()
                .name("user2")
                .email("user2@gmail.com")
                .password("123")
                .profileImgUrl("https://~~")
                .address("사랑시 설렘구 불행동")
                .age(100)
                .build();

        Post post1 = new Post("https://imgUrl1~~", "포스트 1");
        Post post2 = new Post("https://imgUrl2~~", "포스트 2");
        post1.setOwner(user1);
        post2.setOwner(user1);

        Post post3 = new Post("https://imgUrl3~", "포스트 3");
        Post post4 = new Post("https://imgUrl4~~", "포스트 4");
        post3.setOwner(user2);
        post4.setOwner(user2);

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post3);
        entityManager.persist(post4);
    }

    @Test
    void startJPQL() {
        // user1을 찾아라
        User findUser = entityManager.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                .setParameter("name", "user1")
                .getSingleResult();

        assertThat(findUser.getName()).isEqualTo("user1");
    }

    @Test
    void startQuerydsl() {
        User findUser = queryFactory.select(user)
                .from(user)
                .where(user.name.eq("user1"))
                .fetchOne();

        assertThat(findUser.getName()).isEqualTo("user1");
    }

    @Test
    void search() {
        User findUser = queryFactory
                .selectFrom(user)
                .where(user.name.eq("user1")
                        .and(user.age.eq(20)))
                .fetchOne();

        assertThat(findUser.getName()).isEqualTo("user1");
    }

    @Test
    void searchAndParam() {
        User findUser = queryFactory
                .selectFrom(user)
                .where(
                        user.name.eq("user1"),
                        user.age.eq(20)
                )
                .fetchOne();

        assertThat(findUser.getName()).isEqualTo("user1");
    }

    @Test
    void resultFetchTest() {
//        List<User> users = queryFactory
//                .selectFrom(user)
//                .fetch();
//
//        User fetchOne = queryFactory
//                .selectFrom(user)
//                .fetchOne();
//
//        User fetchFirst = queryFactory
//                .selectFrom(user)
//                .fetchFirst();

        QueryResults<User> results = queryFactory
                .selectFrom(user)
                .fetchResults(); // deprecated (그냥 따로따로 사용할 것)

        results.getTotal(); // 데이터 총 개수 (페이징 할 때 사용)
        List<User> userList = results.getResults();

        Long count = queryFactory
                .select(user.count())
                .from(user)
                .fetchOne();
    }

    /**
     * 1. 유저 나이 내림차순 (desc)
     * 2. 유저 이름 오름차순 (asc)
     * 단, 2에서 회원 이름이 없으면 마지막에 출력 (nulls last)
     */
    @Test
    void sort() {
        User user3 = User.builder()
                .name("user3")
                .email("user3@gmail.com")
                .password("123")
                .profileImgUrl("https://~~")
                .address("사랑시 고백구 행복동")
                .age(100)
                .build();

        User user4 = User.builder()
                .name("user4")
                .email("user4@gmail.com")
                .password("123")
                .profileImgUrl("https://~~")
                .address("사랑시 설렘구 불행동")
                .age(100)
                .build();

        entityManager.persist(user3);
        entityManager.persist(user4);

        List<User> users = queryFactory
                .selectFrom(user)
                .where(user.age.eq(100))
                .orderBy(
                        user.age.desc(),
                        user.name.asc().nullsLast()
                )
                .fetch();

        User user5 = users.get(0);
        User user6 = users.get(1);
        User user7 = users.get(2);

        assertThat(user5.getName()).isEqualTo("user2");
        assertThat(user6.getName()).isEqualTo("user3");
        assertThat(user7.getName()).isEqualTo("user4");
    }

    @Test
    void paging() {
        List<User> users = queryFactory
                .selectFrom(user)
                .orderBy(user.name.desc())
                .offset(1) // 0부터 시작. 1이면 한 개 skip
                .limit(2)
                .fetch();

        assertThat(users.size()).isEqualTo(1);
    }

    @Test
    void aggregation() {
        List<Tuple> result = queryFactory
                .select(
                        user.count(),
                        user.age.sum(),
                        user.age.avg(),
                        user.age.max(),
                        user.age.min()
                )
                .from(user)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(user.count())).isEqualTo(2);
        assertThat(tuple.get(user.age.sum())).isEqualTo(120);
        assertThat(tuple.get(user.age.avg())).isEqualTo(60);
        assertThat(tuple.get(user.age.max())).isEqualTo(100);
        assertThat(tuple.get(user.age.min())).isEqualTo(20);
    }


    @Test
    void group() {
        List<Tuple> result = queryFactory
                .select(post.content, user.name)
                .from(post)
                .join(post.user, user)
                .groupBy(user.name)
                .fetch();

        Tuple tupleA = result.get(0);
        Tuple tupleB = result.get(1);

        log.info(tupleA.get(user.name));
        log.info(tupleA.get(post.content));

        log.info(tupleB.get(user.name));
        log.info(tupleB.get(post.content));
    }

    /**
     * user1이 쓴 모든 포스트
     */
    @Test
    void join() {
        List<Post> posts = queryFactory
                .select(post)
                .from(post)
                .join(post.user, user)
                .on(user.name.eq("user1"))
                .fetch();

        assertThat(posts.size()).isEqualTo(2);
        assertThat(posts)
                .extracting("content")
                .containsExactly("포스트 1", "포스트 2");
    }

    /**
     * post와 user를 모두 조인하면서, user 이름이 user1인 user만 조인, user를 모두 조회
     */
    @Test
    void joinOnFiltering() {
        List<Tuple> result = queryFactory
                .select(user, post)
                .from(post)
                .rightJoin(post.user, user).on(user.name.eq("user1"))
                .fetch();

        for (Tuple tuple : result) {
            log.info("tuple={}", tuple);
        }
    }

    @Test
    void noFetchJoin() {
        entityManager.flush();
        entityManager.clear();

        Post findPost = queryFactory
                .selectFrom(post)
                .where(post.content.eq("포스트 1"))
                .fetchOne();

        // 1차 캐시에 로드되어 있는지 확인
        boolean isLoaded = entityManagerFactory.getPersistenceUnitUtil().isLoaded(findPost.getUser());

        assertThat(isLoaded).isFalse();
    }

    @Test
    void fetchJoin() {
        entityManager.flush();
        entityManager.clear();

        Post findPost = queryFactory
                .selectFrom(post)
                .join(post.user, user).fetchJoin()
                .where(post.content.eq("포스트 1"))
                .fetchOne();

        // 1차 캐시에 로드되어 있는지 확인
        boolean isLoaded = entityManagerFactory.getPersistenceUnitUtil().isLoaded(findPost.getUser());

        assertThat(isLoaded).isTrue();
    }

    /**
     * 나이가 가장 많은 조회
     */
    @Test
    void subQuery() {
        QUser subUser = new QUser("subUser"); // subQuery 날리기 위해서 alias 필요

        List<User> users = queryFactory
                .selectFrom(user)
                .where(user.age.eq(
                                JPAExpressions.select(subUser.age.max())
                                        .from(subUser)
                        )
                )
                .fetch();

        assertThat(users).extracting("age")
                .containsExactly(100);
    }

    /**
     * 나이가 평균 이상인 유저
     */
    @Test
    void subQueryGoe() {
        QUser subUser = new QUser("subUser"); // subQuery 날리기 위해서 alias 필요

        List<User> users = queryFactory
                .selectFrom(user)
                .where(user.age.goe(
                                JPAExpressions.select(subUser.age.avg())
                                        .from(subUser)
                        )
                )
                .fetch();

        assertThat(users).extracting("age")
                .containsExactly(100);
    }

    @Test
    void basicCase() {
        List<String> result = queryFactory
                .select(user.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타")
                )
                .from(user)
                .fetch();

        for (String string : result) {
            log.info("string={}", string);
        }
    }

    @Test
    void complexCase() {
        List<String> ages = queryFactory
                .select(new CaseBuilder()
                        .when(user.age.between(0, 20)).then("0 ~ 20살")
                        .when(user.age.between(21, 30)).then("21 ~ 30살")
                        .otherwise("기타")
                )
                .from(user)
                .fetch();

        for (String string : ages) {
            log.info("string={}", string);
        }
    }

    @Test
    void constant() {
        List<Tuple> result = queryFactory
                .select(user.name, Expressions.constant("A"))
                .from(user)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    void concat() {
        // {name}_{age}
        List<String> fetch = queryFactory
                .select(user.name.concat("_").concat(user.age.stringValue()))
                .from(user)
                .where(user.name.eq("user1"))
                .fetch();

        for (String string : fetch) {
            System.out.println("string = " + string);
        }
    }


    // 순수 JPA에서 DTO를 조회할 때
    @Test
    void projectionDtoByJPQL() { // 별로임. 패키지명 언제 다 적고 있니
        List<SignUpResponseDto> resultList = entityManager.createQuery("select new com.sixsense.newsfeed.dto.SignUpResponseDto(u.id, u.name, u.email,u.profileImgUrl, u.age, u.address) from User u", SignUpResponseDto.class)
                .getResultList();

        for (SignUpResponseDto signUpResponseDto : resultList) {
            System.out.println("signUpResponseDto = " + signUpResponseDto);
        }
    }

    @Test
    void projectionDtoByQuerydsl() {
        List<UserResponse> result = queryFactory
                .select(Projections.bean(UserResponse.class,
                        user.id,
                        user.email,
                        user.age,
                        user.profileImgUrl,
                        user.address,
                        user.name
                ))
                .from(user)
                .fetch();

        for (UserResponse userResponse : result) {
            System.out.println("userResponse = " + userResponse);
        }
    }

    @Getter
    @NoArgsConstructor
    @ToString
    @Setter
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private Integer age;
        private String profileImgUrl;
        private String address;

        @QueryProjection
        public UserResponse(Long id, String name, String email, Integer age, String profileImgUrl, String address) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.age = age;
            this.profileImgUrl = profileImgUrl;
            this.address = address;
        }
    }

    @Test
    void projectionDtoByField() {
        QUser subUser = new QUser("subUser");

        List<UserResponse> result = queryFactory
                .select(Projections.fields(UserResponse.class, // getter/setter 무시
                        user.id.as("id"), // 프로퍼티 명이 다를 때는 맞춰줘야 함. (예시)
                        user.name,
                        user.email,
                        ExpressionUtils.as(JPAExpressions
                                .select(subUser.age.min())
                                .from(subUser), "age"
                        ), // 서브쿼리를 사용할 때 Expressions.as() || Expressions.as() 사용
                        user.profileImgUrl,
                        user.address
                ))
                .from(user)
                .fetch();

        for (UserResponse userResponse : result) {
            System.out.println("userResponse = " + userResponse);
        }
    }

    @Test
    void projectionDtoByConstructor() {
        List<UserResponse> result = queryFactory
                .select(Projections.constructor(UserResponse.class,
                        user.id,
                        user.name,
                        user.email,
                        user.age,
                        user.profileImgUrl,
                        user.address
                ))
                .from(user)
                .fetch();

        for (UserResponse userResponse : result) {
            System.out.println("userResponse = " + userResponse);
        }
    }

    @Test
    void projectionDtoByQueryProjection() {
        List<UserResponse> responses = queryFactory
                .select(new QQuerydslBasicTest_UserResponse(
                        user.id,
                        user.name,
                        user.email,
                        user.age,
                        user.profileImgUrl,
                        user.address
                ))
                .from(user)
                .fetch();

        for (UserResponse result : responses) {
            System.out.println("result = " + result);
        }
    }

    // 동적 쿼리 1
    @Test
    void dynamicQuery_BooleanBuilder() {
        String name = "user1";
//        Integer ageParam = 20;
        Integer ageParam = null;

        List<User> users = searchUser1(name, ageParam);
        assertThat(users.size()).isEqualTo(1);
    }

    private List<User> searchUser1(String nameCond, Integer ageCond) {

        BooleanBuilder builder = new BooleanBuilder();
        if (nameCond != null) {
            builder.and(user.name.eq((nameCond)));
        }

        if (ageCond != null) {
            builder.and(user.age.eq(ageCond));
        }

        return queryFactory
                .selectFrom(user)
                .where(builder)
                .fetch();
    }

    // 동적 쿼리2 (영한샘이 선호하는 방식)
    @Test
    void dynamicQuery_WhereParam() {
        String name = "user1";
//        Integer ageParam = 20;
        Integer ageParam = null;

        List<User> users = searchUser2(name, ageParam);
        assertThat(users.size()).isEqualTo(1);
    }

    private List<User> searchUser2(String nameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(user)
                .where(allEq(nameCond, ageCond))
//                .where(nameEq(nameCond), ageEq(ageCond)) // 조건 여러 개 나열하면 AND, `null`이면 무시
                .fetch();
    }

    private BooleanExpression allEq(String nameCond, Integer ageCond) { // 조립도 가능하다.
        return nameEq(nameCond).and(ageEq(ageCond));
    }

    private BooleanExpression nameEq(String nameCond) {
        return nameCond != null ? user.name.eq(nameCond) : null;
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? user.age.eq(ageCond) : null;
    }

    /**
     * 수정, 삭제 벌크 연산
     * 벌크 연산은 연속성 컨텍스트와 동기화가 되지 않는다.
     * 즉, 연속성 컨텍스트에 엔티티를 무시하고 쿼리를 날리기 때문에
     * 쿼리 실행 후 연속성 컨텍스트와 내용이 달라질 수 있다.
     * -> 모든 벌크 연산은 1차 캐시 값을 무시하고 DB로 쿼리를 날리기 때문에 1차 캐시의 엔티티 정보와 동기화가 안 된다.
     * 그렇기에 벌크 연산을 수행하면 그 다음에 무조건 다음 두 개의 코드를 실행하자.
     * entityManager.flush();
     * entityManager.clear();
     */
    @Test
    void bulkUpdate() {
        long count = queryFactory
                .update(user)
                .set(user.name, "비회원")
                .where(user.age.lt(28))
                .execute(); // 영향을 받을 Row 수 반환

        assertThat(count).isEqualTo(1);

        List<User> users = queryFactory
                .selectFrom(user)
                .fetch();
        for (User user1 : users) {
            System.out.println("user1 = " + user1);
        }
    }

    @Test
    void bulkAdd() {
        queryFactory
                .update(user)
                .set(user.age, user.age.add(1))
                .execute();
    }

    // SQL Function 호출하기
    @Test
    void sqlFunction() {
        List<String> result = queryFactory
                .select(
                        Expressions.stringTemplate("function('replace', {0}, {1}, {2})",
                                user.name, "user", "M"
                        )
                )
                .from(user)
                .fetch();

        for (String string : result) {
            System.out.println("string = " + string);
        }
    }

    @Test
    void sqlFunction2() {
        List<String> result = queryFactory
                .select(user.name)
                .from(user)
//                .where(user.name.eq(
//                        Expressions.stringTemplate("function('upper', {0})", user.name)
//                ))
                .where(user.name.eq(user.name.upper()))
                .fetch();

        for (String string : result) {
            System.out.println("string = " + string);
        }
    }

}
