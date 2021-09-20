package imc4k.church.booking.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Document("user")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String nickName;
    private String email;

    private Date addedDate;
    private Date updatedDate;
    private String approvedBy;
}
