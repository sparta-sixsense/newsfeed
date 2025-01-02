package com.sixsense.newsfeed.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.util.List;

import static com.sixsense.newsfeed.querydsl.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
public class ReflectionTest {

    public record MemberResponse(Long id, String username, String email, Integer age) {
    }

    @Autowired
    EntityManager entityManager;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void beforeEach() {
        queryFactory = new JPAQueryFactory(entityManager);

        Member member1 = Member.builder()
                .username("member1")
                .email("member1@gmail.com")
                .age(10)
                .build();

        Member member2 = Member.builder()
                .username("member2")
                .email("member3@gmail.com")
                .age(20)
                .build();

        Member member3 = Member.builder()
                .username("member3")
                .email("member3@gmail.com")
                .age(30)
                .build();

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.persist(member3);
    }

    @Test
    void querydslConstructorTest() {
        List<MemberResponse> results = queryFactory
                .select(Projections.constructor(MemberResponse.class,
                                member.id,
                                member.username,
                                member.email,
                                member.age
                        )
                )
                .from(member)
                .fetch();

        for (MemberResponse result : results) {
            System.out.println("result = " + result);
        }

        assertThat(results.size()).isEqualTo(3);
    }

    @Test
    void reflectionTest() throws Exception {
        Constructor<MemberResponse> constructor = MemberResponse.class.getConstructor(Long.class, String.class, String.class, Integer.class);

        Object[] parameterValues = {1L, "member1", "member@gmail.com", 10};
        MemberResponse memberResponse = constructor.newInstance(parameterValues);

        log.info("memberResponse={}", memberResponse);
    }
}
