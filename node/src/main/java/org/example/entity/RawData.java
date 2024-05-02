package org.example.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
//@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "raw_data")
@TypeDef(name = "jsonb",typeClass = JsonBinaryType.class)
public class RawData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//jsonb - это фича постгреса, которая представляет из себя оптимизированную двоичную
// разновидность формата json у которой пробелы удаляются, сортировка объектов внутри
// json не сохраняется порядок их, также не сохраняются ключи дубликаты, вместо этого последний ключ
// он будет перезаписан - это та плата, которая нужна за оптимизацию, но при этом когда мы обращаемся к
// данным типа jsonb они хранятся в бд не в виде строки, а в виде полноценного json по которому можно
// навигироваться с помощью спец. запросов
    @Type(type = "jsonb")
    @Column(name = "event",columnDefinition = "jsonb")
    private Update event;


}
