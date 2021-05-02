package com.jokey.myblog.common.dao;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JPA 동적 Where 조건문 생성
 *
 * @param <T>
 */
public class CustomSpecifications<T> implements Specification<T> {

    // where clause list
    protected List<List<SearchCriteria<?>>> list;

    public CustomSpecifications() {
        this.list = new ArrayList<>();
    }

    public final void add( List<SearchCriteria<?>> criteria ) {
        list.add( criteria );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Predicate toPredicate( Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder ) {
        // create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        // TODO jdk 1.8 stream 구문으로 교체
        for( List<SearchCriteria<?>> criteriaList : list ) {
            if( criteriaList.size() > 1 ) {
                List<Predicate> subPredicates = new ArrayList<>();
                for( SearchCriteria<?> criteria : criteriaList ) {
                    this.addPredicates( criteria, subPredicates, criteriaBuilder, root );
                }
                Predicate orArrays = criteriaBuilder.or( subPredicates.toArray( new Predicate[0] ) );
                predicates.add( orArrays );
            } else {
                this.addPredicates( criteriaList.get( 0 ), predicates, criteriaBuilder, root );
            }
        }

        return criteriaBuilder.and( predicates.toArray( new Predicate[0] ) );
    }

    /**
     * Where 구문의 () 를 생성하기 위한 predicate 그룹핑
     * <pre>
     *     ex) WHERE 1=1 AND ( A = 'a' OR A = 'b' )
     * </pre>
     *
     * @param criteria        SearchCriteria
     *                        // * @param predicates List<Predicate>
     * @param criteriaBuilder CriteriaBuilder
     * @param root            Root<T>
     */
    private void addPredicates( SearchCriteria<?> criteria, List<Predicate> predicates, CriteriaBuilder criteriaBuilder, Root<T> root ) {
        if( criteria.getOperation().equals( SearchCriteria.SearchOperation.IN ) )
            predicates.add( criteriaBuilder.in( root.get( criteria.getKey() ) ).value( criteria.getList() ) );
        else if( criteria.getOperation().equals( SearchCriteria.SearchOperation.NOT_EQUAL ) )
            predicates.add( criteriaBuilder.notEqual( root.get( criteria.getKey() ), criteria.getValue() ) );
        else if( criteria.getOperation().equals( SearchCriteria.SearchOperation.EQUAL ) )
            predicates.add( criteriaBuilder.equal( root.get( criteria.getKey() ), criteria.getValue() ) );
        else if( criteria.getOperation().equals( SearchCriteria.SearchOperation.MATCH ) )
            predicates.add( criteriaBuilder.like( root.get( criteria.getKey() ), "%" + criteria.getValue() + "%" ) );
    }
}