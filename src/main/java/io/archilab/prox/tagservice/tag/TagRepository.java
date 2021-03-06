package io.archilab.prox.tagservice.tag;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends PagingAndSortingRepository<Tag, UUID>, TagRepositoryCustom {

  Set<Tag> findByTagName_TagName(@Param(value = "tagName") String tagName);

  Set<Tag> findByTagName_TagNameContaining(@Param(value = "tagName") String tagName);
}
