package com.example.jdbc.service;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repsository.MemberRepositoryV3;
import java.sql.SQLException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
public class MemberServiceV3_2 {

  private final TransactionTemplate txTemplate;
  private final MemberRepositoryV3 memberRepository;

  public MemberServiceV3_2(PlatformTransactionManager transactionManager,
      MemberRepositoryV3 memberRepository) {

    // TransactionTemplate을 직접 주입받으면 클래스이고 기술별 확장 클래스가 따로 없어 변경이 어렵기 떄문에 관례 상 트랜잭션 매니저를 주입받아서 사용한다.
    this.txTemplate = new TransactionTemplate(transactionManager);
    this.memberRepository = memberRepository;
  }

  public void accountTransfer(String fromId, String toId, int money) throws SQLException {
    txTemplate.executeWithoutResult((status) -> {
      try {
        // 비즈니스 로직
        bizLogic(fromId, toId, money);
      } catch (SQLException e) {
        throw new IllegalStateException(e);
      }
    });
  }

  private void bizLogic(String fromId, String toId, int money) throws SQLException {
    Member fromMember = memberRepository.findById(fromId);
    Member toMember = memberRepository.findById(toId);
    memberRepository.update(fromId, fromMember.getMoney() - money);
    validation(toMember);
    memberRepository.update(toId, toMember.getMoney() + money);
  }

  private void validation(Member toMember) {
    if (toMember.getMemberId().equals("ex")) {
      throw new IllegalStateException("이체중 예외 발생");
    }
  }
}
