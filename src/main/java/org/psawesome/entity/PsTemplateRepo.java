package org.psawesome.entity;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Repository
@RequiredArgsConstructor
public class PsTemplateRepo {

  private final ConnectionFactory connectionFactory;

  Flux<PsTemplate> findAll() {
    return this.connection().flatMapMany(connection ->
            Flux.from(connection.createStatement("select * from TTB_CMSC_TEMPLATE").execute())
                    .log()
                    .flatMap(r -> r.map(this::setDto)));
  }

  Flux<PsTemplate> findOne(Integer id) {
    /*
    TODO
java.lang.UnsupportedOperationException: Binding parameters is not supported for the statement [SELECT * FROM TTB_CMSC_TEMPLATE WHERE TEMPLATE_SEQ = $1]
	at io.r2dbc.mssql.SimpleMssqlStatement.bind(SimpleMssqlStatement.java:93) ~[r2dbc-mssql-0.8.5.RELEASE.jar:0.8.5.RELEASE]
	at io.r2dbc.mssql.SimpleMssqlStatement.bind(SimpleMssqlStatement.java:43) ~[r2dbc-mssql-0.8.5.RELEASE.jar:0.8.5.RELEASE]
	at org.psawesome.entity.PsTemplateRepo.lambda$findOne$3(PsTemplateRepo.java:32) ~[main/:na]
    */
    return this.connection().flatMapMany(connection ->
            Flux.from(connection.createStatement("SELECT * FROM TTB_CMSC_TEMPLATE WHERE TEMPLATE_SEQ = $1")
                    .fetchSize(1)
                    .bind(0, id)
                    .execute())
                    .log()
                    .flatMap(r -> r.map(this::setDto)));
  }

  private Mono<Connection> connection() {
    return Mono.from(this.connectionFactory.create());
  }

  private PsTemplate setDto(Row row, RowMetadata rowMetadata) {
    return PsTemplate.builder()
            .templateSeq(row.get("TEMPLATE_SEQ", Integer.class))
            .siteCd(row.get("SITE_CD", String.class))
            .templateId(row.get("TEMPLATE_ID", String.class))
            .descript(row.get("DESCRIPT", String.class))
            .inout(row.get("INOUT", String.class))
            .useYn(row.get("USE_YN", String.class))
            .modSeq(row.get("MOD_SEQ", String.class))
            .modDt(row.get("MOD_DT", LocalDateTime.class))
            .regSeq(row.get("REG_SEQ", String.class))
            .regDt(row.get("REG_DT", LocalDateTime.class))
            .build();
  }
}
