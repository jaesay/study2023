package hello.core.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Order {

  private Long memberId;
  private String itemName;
  private int itemPrice;
  private int discountPrice;
}
