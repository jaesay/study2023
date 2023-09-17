package com.example.hexarch.member.adapter.out.persistence;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
class MemberEntity {

  @Id
  @Column("member_id")
  private Long id;
  private String name;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column("last_modified_at")
  private LocalDateTime modifiedAt;
}
