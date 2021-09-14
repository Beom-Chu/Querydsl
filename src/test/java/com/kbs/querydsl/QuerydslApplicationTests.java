package com.kbs.querydsl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import com.kbs.querydsl.entity.Hello;
import com.kbs.querydsl.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
//@Commit
class QuerydslApplicationTests {

  @PersistenceContext
  private EntityManager em;
  
	@Test
	void contextLoads() {
	  
	  Hello hello = new Hello();
	  em.persist(hello);
	  
	  JPAQueryFactory query = new JPAQueryFactory(em);
	  QHello qHello = QHello.hello;
	  
	  Hello result = query
        	      .selectFrom(qHello)
        	      .fetchOne();
	  
	  Assertions.assertThat(result).isEqualTo(hello);
	  Assertions.assertThat(result.getId()).isEqualTo(hello.getId());
	  
	}

}
