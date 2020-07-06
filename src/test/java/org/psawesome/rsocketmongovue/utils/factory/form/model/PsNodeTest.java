package org.psawesome.rsocketmongovue.utils.factory.form.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.psawesome.rsocketmongovue.utils.factory.form.model.type.impl.PsArray;
import org.psawesome.rsocketmongovue.utils.factory.form.model.type.impl.PsDate;
import org.psawesome.rsocketmongovue.utils.factory.form.model.type.impl.PsMap;
import org.psawesome.rsocketmongovue.utils.factory.form.model.type.impl.PsString;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author ps [https://github.com/wiv33/rsocket-mongo-vue]
 * @role
 * @responsibility
 * @cooperate {
 * input:
 * output:
 * }
 * @since 20. 7. 4. Saturday
 */
public class PsNodeTest {
  // input
  String strLinux, strWin;
  ObjectMapper mapper;
  protected List<LinkedHashMap<String, Object>> linked, windowLinked;
  Flux<LinkedHashMap<String, Object>> publisher, winpub;

  @BeforeEach
  public void setUp() throws IOException {
//    strLinux = Files.readString(Path.of("/home/ps/dev/java/IdeaProjects/rsocket-mongo-vue/src/test/java/org/psawesome/rsocketmongovue/utils/factory/form/model/input-one-depth.json"));
    strWin = Files.readString(Path.of("C:\\private\\projects\\rsocket-mongo-vue\\src\\test\\java\\org\\psawesome\\rsocketmongovue\\utils\\factory\\form\\model\\input-one-depth.json"));
    mapper = new ObjectMapper();
//    linked = mapper.readValue(strLinux, new TypeReference<>() {
//    });
    windowLinked = mapper.readValue(strWin, new TypeReference<>() {
    });

//    publisher = Flux.fromStream(getStream())
//            .log();
    winpub = Flux.fromStream(windowLinked.stream()
//            .peek(System.out::println)
    ).log();
  }

  private Stream<LinkedHashMap<String, Object>> getStream() {
    return linked.stream().peek(System.out::println);
  }

  @Test
  @Deprecated
  void testMultiDepthJson() throws IOException {
    String str = Files.readString(Path.of("/home/ps/dev/java/IdeaProjects/rsocket-mongo-vue/src/test/java/org/psawesome/rsocketmongovue/utils/factory/form/model/input-multi-depth.json"));
    ObjectMapper mapper = new ObjectMapper();
    LinkedHashMap<String, Object> stringObjectMap = mapper.readValue(str, new TypeReference<>() {
    });
  }

  @Test
  void testOneDepthJson() throws IOException {

    StepVerifier.create(Flux.fromStream(getStream())
            .map(PsNode::new)
            .map(node -> node.getValue().getClass().getName())
            .log()
    )
            .expectNext(PsMap.class.getName(),
                    PsString.class.getName(),
                    PsString.class.getName(),
                    PsDate.class.getName(),
                    PsArray.class.getName(),
                    PsMap.class.getName(),
                    PsDate.class.getName(),
                    PsString.class.getName(),
                    PsString.class.getName())
            .verifyComplete();
  }

  @Test
  void testNodeGetValue() {
    StepVerifier.create(Flux.fromStream(getStream())
            .map(PsNode::new)
            .map(objectPsNode -> objectPsNode.getValue().getValue())
            .log())
            .expectNextCount(9)
            .verifyComplete();
  }

  @Test
  void testNodeSet() {
    StepVerifier.create(publisher.map(PsNode::new))
            .consumeNextWith(node -> assertAll(
                    () -> assertNull(node.getParent()),
                    () -> assertNotNull(node.getName()),
                    () -> assertNotNull(node.getAttributes()),
                    () -> assertNotNull(node.getCdata()),
                    () -> assertNotNull(node.getSpell())
            ))
            .consumeNextWith(node -> assertAll(
                    () -> assertNotNull(node.getName()),
                    () -> assertNotNull(node.getAttributes()),
                    () -> assertNotNull(node.getParent()),
                    () -> assertNotNull(node.getCdata()),
                    () -> assertNotNull(node.getSpell())
            ))
            .thenCancel()
            .verify();
  }

  @Test
  void testJson() {
    winpub.map(PsNode::new)
            .log()
            .map(value -> {
              try {
                System.out.println("value = " + value);
                String s = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(value);
                System.out.println("s = " + s);
                return s;
              } catch (JsonProcessingException e) {
                return e.getMessage();
              }
            })
    .log()
    .subscribe(System.out::println);
  }
}