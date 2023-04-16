package com.example.springbootrediscache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

  private final ItemEntityRepository itemEntityRepository;

  @Cacheable(value = "itemCache")
  public ItemEntity getItemForId(long id) {
    // 첫 요청
    // "GET" "itemCache::1"
    // "SET" "itemCache::1" "\xac\xed\x00\x05sr\x00+com.example.springbootrediscache.ItemEntity\x82\x02R\x89<\xc1>\x99\x02\x00\x02L\x00\x0bdescriptiont\x00\x12Ljava/lang/String;L\x00\x02idt\x00\x10Ljava/lang/Long;xpt\x00\x05ITEM1sr\x00\x0ejava.lang.Long;\x8b\xe4\x90\xcc\x8f#\xdf\x02\x00\x01J\x00\x05valuexr\x00\x10java.lang.Number\x86\xac\x95\x1d\x0b\x94\xe0\x8b\x02\x00\x00xp\x00\x00\x00\x00\x00\x00\x00\x01" "PX" "600000"
    // 이후 요청
    // "GET" "itemCache::1"
    return itemEntityRepository.findById(id)
        .orElseThrow(RuntimeException::new);
  }
}
