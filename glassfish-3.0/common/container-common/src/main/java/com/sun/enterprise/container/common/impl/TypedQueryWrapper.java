/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.enterprise.container.common.impl;


import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;

/**
 * Wrapper class for javax.persistence.TypedQuery objects returned from
 * non-transactional access of a container-managed transactional
 * EntityManager.
 *
 * @see QueryWrapper for more details about why the wrapper is needed
 */
public class TypedQueryWrapper<X> extends QueryWrapper implements TypedQuery<X> {

    private CriteriaQuery<X> queryCriteria;
    private Class<X> queryresultClassType;

    public static <X> TypedQuery<X> createQueryWrapper(EntityManagerFactory emf, Map emProperties,
                                                       EntityManager emDelegate,TypedQuery<X> queryDelegate,
                                                       CriteriaQuery<X> criteriaQuery) {
        return new TypedQueryWrapper<X>(emf, emProperties, emDelegate, queryDelegate, criteriaQuery);
    }

    public static <X> TypedQuery<X> createQueryWrapper(EntityManagerFactory emf, Map emProperties,
                                                       EntityManager emDelegate, TypedQuery<X> queryDelegate,
                                                       String ejbqlString, Class<X> resultClassType) {

        return new TypedQueryWrapper<X>(emf, emProperties, emDelegate, queryDelegate,
                QueryType.TYPED_EJBQL, ejbqlString, resultClassType);
    }


    public static <X> TypedQuery<X> createNamedQueryWrapper(EntityManagerFactory emf, Map emProperties,
                                                            EntityManager emDelegate, TypedQuery<X> queryDelegate,
                                                            String name, Class<X> resultClassType) {

        return new TypedQueryWrapper<X>(emf, emProperties, emDelegate, queryDelegate,
                QueryType.TYPED_NAMED, name, resultClassType);
    }

    private TypedQueryWrapper(EntityManagerFactory emf, Map emProperties, EntityManager emDelegate,
                              TypedQuery<X> qDelegate, CriteriaQuery<X> criteriaQuery) {
        super(emf, emProperties, emDelegate, qDelegate, QueryType.TYPED_CRITERIA, null, null, null);
        this.queryCriteria = criteriaQuery;
    }

    private TypedQueryWrapper(EntityManagerFactory emf, Map emProperties, EntityManager emDelegate,
                              TypedQuery<X> qDelegate, QueryType queryType, String queryString, Class<X> resultClass) {

        super(emf, emProperties, emDelegate, qDelegate, queryType, queryString, null, null);
        this.queryresultClassType = resultClass;
    }

    @Override
    protected TypedQuery<X> createQueryDelegate(QueryType queryType, EntityManager entityManager, String queryString) {
        TypedQuery<X> retVal;

        switch (queryType) {
            case TYPED_CRITERIA:
                retVal = entityManager.createQuery(queryCriteria);
                break;

            case TYPED_EJBQL:
                retVal = entityManager.createQuery(queryString, queryresultClassType);
                break;

            case TYPED_NAMED:
                retVal = entityManager.createNamedQuery(queryString, queryresultClassType);
                break;

            default:
                assert false : "The method is called with unexpected queryType.";
                retVal = null;

        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<X> getResultList() {
        // If this method is called, the current instance is guranted to be of type TypedQuery<X>
        // It is safe to cast here.
        return (List<X>) super.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public X getSingleResult() {
        // If this method is called, the current instance is guranted to be of type TypedQuery<X>
        // It is safe to cast here.
        return (X) super.getSingleResult();
    }

    @Override
    public TypedQuery<X> setMaxResults(int maxResults) {
        super.setMaxResults(maxResults);
        return this;
    }

    @Override
    public TypedQuery<X> setFirstResult(int startPosition) {
        super.setFirstResult(startPosition);
        return this;
    }

    @Override
    public TypedQuery<X> setHint(String hintName, Object value) {
        super.setHint(hintName, value);
        return this;
    }

    @Override
    public <T> TypedQuery<X> setParameter(Parameter<T> param, T value) {
        super.setParameter(param, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<Date> param, Date value,  TemporalType temporalType) {
       super.setParameter(param, value, temporalType);
       return this;
   }

    @Override
    public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value,  TemporalType temporalType) {
       super.setParameter(param, value, temporalType);
       return this;
   }

    @Override
    public TypedQuery<X> setParameter(String name, Object value) {
       super.setParameter(name, value);
       return this;
   }

    @Override
    public TypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) {
       super.setParameter(name, value, temporalType);
       return this;
   }

    @Override
    public TypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) {
       super.setParameter(name, value, temporalType);
       return this;
   }

    @Override
    public TypedQuery<X> setParameter(int position, Object value) {
       super.setParameter(position, value);
       return this;
   }

    @Override
    public TypedQuery<X> setParameter(int position, Date value,  TemporalType temporalType) {
       super.setParameter(position, value, temporalType);
       return this;
   }

    @Override
    public TypedQuery<X> setParameter(int position, Calendar value,  TemporalType temporalType) {
       super.setParameter(position, value, temporalType);
       return this;
   }

    public <T> Parameter<T> getParameter(String name, Class<T> type) {
       return (Parameter<T>) super.getParameter(name);
   }

    public <T> Parameter<T> getParameter(int position, Class<T> type) {
       return (Parameter<T>) super.getParameter(position);
   }

    @Override
    public TypedQuery<X> setFlushMode(FlushModeType flushMode) {
        super.setFlushMode(flushMode);
        return this;
    }

    @Override
    public TypedQuery<X> setLockMode(LockModeType lockModeType) {
        super.setLockMode(lockModeType);
        return this;
    }
}