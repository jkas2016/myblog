package com.jokey.myblog.common.dao;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JPA 동적 Where 조건문 생성
 *
 * @param <T>
 */
public class CustomSpecifications<T> implements Specification<T> {

    // where clause list
    protected List<SearchCriteria<?>[]> list;

    public CustomSpecifications() {
        this.list = new ArrayList<>();
    }

    /**
     * WHERE 조건 등록
     *
     * @param criteria 가변인자
     */
    public final void add( SearchCriteria<?>... criteria ) {
        list.add( criteria );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Predicate toPredicate( Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder ) {
        // WHERE 구문 AND 조건 생성
        return criteriaBuilder.and(
                list.stream()
                        .map( criteriaList -> {
                            if( criteriaList.length > 1 ) {
                                // Where 구문의 () 를 생성하기 위한 predicate 그룹핑
                                // 괄호 내부는 OR 조건 처리
                                return criteriaBuilder.or(
                                        Arrays.stream( criteriaList )
                                                .map( x -> this.addPredicates( x, criteriaBuilder, root ) )
                                                .toArray( Predicate[]::new )
                                );
                            } else {
                                return this.addPredicates( criteriaList[0], criteriaBuilder, root );
                            }
                        } )
                        .toArray( Predicate[]::new )
        );
    }

    /**
     * Where 조건 별 SQL 구문 생성
     *
     * @param criteria        SearchCriteria
     * @param criteriaBuilder CriteriaBuilder
     * @param root            Root<T>
     * @return Predicate
     */
    private Predicate addPredicates( SearchCriteria<?> criteria, CriteriaBuilder criteriaBuilder, Root<T> root ) {
        if( criteria.getOperation().equals( SearchCriteria.SearchOperation.IN ) )
            return criteriaBuilder.in( root.get( criteria.getKey() ) ).value( criteria.getList() );
        else if( criteria.getOperation().equals( SearchCriteria.SearchOperation.NOT_EQUAL ) )
            return criteriaBuilder.notEqual( root.get( criteria.getKey() ), criteria.getValue() );
        else if( criteria.getOperation().equals( SearchCriteria.SearchOperation.EQUAL ) )
            return criteriaBuilder.equal( root.get( criteria.getKey() ), criteria.getValue() );
        else if( criteria.getOperation().equals( SearchCriteria.SearchOperation.MATCH ) )
            return criteriaBuilder.like( root.get( criteria.getKey() ), "%" + criteria.getValue() + "%" );
        return null;
    }
}