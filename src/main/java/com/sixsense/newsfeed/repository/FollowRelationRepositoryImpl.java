package com.sixsense.newsfeed.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sixsense.newsfeed.domain.FollowRelation;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.sixsense.newsfeed.domain.QFollowRelation.followRelation;

@Slf4j
public class FollowRelationRepositoryImpl implements FollowRelationRepositoryForQuerydsl {

    private final JPAQueryFactory queryFactory;

    public FollowRelationRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<FollowRelation> findByRequesterIdAndAccepterId(Long requesterId, Long accepterId) {
        log.debug("QueryDSL Implementation Called");
        log.info("INFO enabled: {}", log.isInfoEnabled());
        log.debug("DEBUG enabled: {}", log.isDebugEnabled());
        log.trace("TRACE enabled: {}", log.isTraceEnabled());

        return Optional.ofNullable(
                queryFactory
                        .select(followRelation)
                        .from(followRelation)
                        .where(
                                followRelation.requester.id.eq(requesterId),
                                followRelation.accepter.id.eq(accepterId)
                        )
                        .fetchOne()
        );
    }

    // Pageable 예시
    public Page<FollowRelation> searchSimplePage(Pageable pageable) {
        List<FollowRelation> results = queryFactory
                .select(followRelation)
                .from(followRelation)
                .where(followRelation.accepter.isNotNull()) // 그냥 넣어본 조건
                .offset(pageable.getOffset()) // 몇 번까지 skip하고 몇 번부터 시작할 거니?
                .limit(pageable.getPageSize()) // 한 번 조회할 때 몇 개까지 조회할 거니?
                .fetch();

        // 총 개수 따로 구해야 함 (`fetchResults()` is deprecated)
//        Long count = queryFactory
//                .select(followRelation.count())
//                .from(followRelation)
//                .where(followRelation.accepter.isNotNull()) // 그냥 넣어본 조건
//                .fetchOne();
//
//        return new PageImpl<>(results, pageable, count);

        // 위의 방법은 일반적인 총 데이터 개수 가져오는 방법.
        // 하지만 마지막 페이지일 경우에는? 혹은 한 페이지에 담길 만큼 데이터다 적은 경우에는?
        JPAQuery<Long> countQuery = queryFactory
                .select(followRelation.count())
                .from(followRelation)
                .where(followRelation.accepter.isNotNull());// 그냥 넣어본 조건

        // 조건이 만족할 때만 카운트 쿼리 날림. 첫 번째 페이지와 마지막 페이지에만 실행
        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }
}
