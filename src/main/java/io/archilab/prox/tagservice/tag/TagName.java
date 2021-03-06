package io.archilab.prox.tagservice.tag;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagName implements Comparable<TagName> {

  private static final int MAX_LENGTH = 40;

  @Column(length = MAX_LENGTH)
  private String tagName;

  public TagName(String tagName) {
    if (!TagName.isValid(tagName)) {
      throw new IllegalArgumentException(
          String.format(
              "Name %s exceeded maximum number of %d allowed characters",
              tagName, TagName.MAX_LENGTH));
    }
    this.tagName = tagName.toLowerCase();
  }

  public static boolean isValid(String tagName) {
    return tagName != null && tagName.length() <= TagName.MAX_LENGTH;
  }

  @Override
  public int compareTo(TagName o) {
    return this.getTagName().compareTo(o.getTagName());
  }

  public void setTagName(String tagName) {
    this.tagName = tagName.toLowerCase();
  }
}
