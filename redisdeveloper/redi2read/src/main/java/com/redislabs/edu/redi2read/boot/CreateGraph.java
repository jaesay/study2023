package com.redislabs.edu.redi2read.boot;

import com.redislabs.edu.redi2read.repository.BookRatingRepository;
import com.redislabs.edu.redi2read.repository.BookRepository;
import com.redislabs.edu.redi2read.repository.CategoryRepository;
import com.redislabs.edu.redi2read.repository.UserRepository;
import com.redislabs.redisgraph.impl.api.RedisGraph;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(8)
@Slf4j
public class CreateGraph implements CommandLineRunner {

  private final RedisTemplate<String, String> redisTemplate;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final BookRatingRepository bookRatingRepository;
  private final CategoryRepository categoryRepository;
  private final String graphId;

  public CreateGraph(RedisTemplate<String, String> redisTemplate, UserRepository userRepository,
      BookRepository bookRepository, BookRatingRepository bookRatingRepository,
      CategoryRepository categoryRepository, @Value("${app.graphId}") String graphId) {

    this.redisTemplate = redisTemplate;
    this.userRepository = userRepository;
    this.bookRepository = bookRepository;
    this.bookRatingRepository = bookRatingRepository;
    this.categoryRepository = categoryRepository;
    this.graphId = graphId;
  }

  @Override
  public void run(String... args) throws Exception {
    if (!redisTemplate.hasKey(graphId)) {
      try (RedisGraph graph = new RedisGraph()) {
        // create an index for Books on id
        graph.query(graphId, "CREATE INDEX ON :Book(id)");
        graph.query(graphId, "CREATE INDEX ON :Category(id)");
        graph.query(graphId, "CREATE INDEX ON :Author(name)");
        graph.query(graphId, "CREATE INDEX ON :User(id)");

        Set<String> authors = new HashSet<>();

        // for each category create a graph node
        categoryRepository.findAll().forEach(c -> {
          graph.query(graphId, String.format("CREATE (:Category {id: \"%s\", name: \"%s\"})", c.getId(), c.getName()));
        });

        // for each book create a graph node
        bookRepository.findAll().forEach(b -> {
          graph.query(graphId, String.format("CREATE (:Book {id: \"%s\", title: \"%s\"})", b.getId(), b.getTitle()));
          // for each author create an AUTHORED relationship to the book
          if (b.getAuthors() != null) {
            b.getAuthors().forEach(a -> {
              if (!authors.contains(a)) {
                graph.query(graphId, String.format("CREATE (:Author {name: \"%s\"})", a));
                authors.add(a);
              }
              graph.query(graphId, String.format(
                  "MATCH (a:Author {name: \"%s\"}), (b:Book {id: \"%s\"}) CREATE (a)-[:AUTHORED]->(b)", a, b.getId()));
            });

            b.getCategories().forEach(c -> {
              graph.query(graphId,
                  String.format("MATCH (b:Book {id: \"%s\"}), (c:Category {id: \"%s\"}) CREATE (b)-[:IN]->(c)",
                      b.getId(), c.getId()));
            });
          }
        });

        // for each user create a graph node
        userRepository.findAll().forEach(u -> {
          graph.query(graphId, String.format("CREATE (:User {id: \"%s\", name: \"%s\"})", u.getId(), u.getName()));

          // for each of the user's book create a purchased relationship
          u.getBooks().forEach(book -> {
            graph.query(graphId,
                String.format("MATCH (u:User {id: \"%s\"}), (b:Book {id: \"%s\"}) CREATE (u)-[:PURCHASED]->(b)",
                    u.getId(), book.getId()));
          });
        });

        // for each book rating create a rated relationship
        bookRatingRepository.findAll().forEach(br -> {
          graph.query(graphId,
              String.format("MATCH (u:User {id: \"%s\"}), (b:Book {id: \"%s\"}) CREATE (u)-[:RATED {rating: %s}]->(b)",
                  br.getUser().getId(), br.getBook().getId(), br.getRating()));
        });
      }

      log.info(">>>> Created graph...");
    }
  }
}
