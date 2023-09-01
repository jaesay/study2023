package com.example.springreactive.chapter14;

import java.util.List;

public interface CryptoCurrencyPriceListener {

  void onPrice(List<Integer> priceList);
  void onComplete();
}
