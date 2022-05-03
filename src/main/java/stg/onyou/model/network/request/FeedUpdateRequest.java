package stg.onyou.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stg.onyou.model.AccessModifier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedUpdateRequest {

    String content;
    AccessModifier access; // public, private
}
