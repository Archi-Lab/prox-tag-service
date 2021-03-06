package io.archilab.prox.tagservice.tag.recommendation;

import io.archilab.prox.tagservice.tag.Tag;
import io.archilab.prox.tagservice.tag.TagRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TagRecommendationCalculator {

  @Autowired private TagCounterRepository tagCounterRepository;

  @Autowired private TagRepository tagRepository;

  @Value("${tagRecommendationCalculation.resultCount}")
  private int resultCount;

  public List<Tag> getRecommendedTags(UUID[] tagIds) {
    // Retrieve search tags
    List<Tag> searchTags = new ArrayList<>();
    for (UUID tagId : tagIds) {
      Optional<Tag> optionalSearchTag = tagRepository.findById(tagId);
      if (optionalSearchTag.isPresent()) {
        searchTags.add(optionalSearchTag.get());
      }
    }

    if (searchTags.isEmpty()) {
      return new ArrayList<>();
    }

    // Score Tags
    Map<Tag, Integer> recommendedTags = new HashMap<>();
    for (Tag searchTag : searchTags) {
      List<TagCounter> tagCounters = tagCounterRepository.findByTag1OrTag2(searchTag, searchTag);
      for (TagCounter tagCounter : tagCounters) {
        Tag otherTag = tagCounter.getOtherTag(searchTag);
        if (!searchTags.contains(otherTag)) {
          Integer count = recommendedTags.get(otherTag);
          if (count == null) {
            recommendedTags.put(otherTag, tagCounter.getCount());
          } else {
            recommendedTags.put(otherTag, count + tagCounter.getCount());
          }
        }
      }
    }

    // Sort recommended tags
    List<Entry<Tag, Integer>> sortedRecommendedTags = new ArrayList<>(recommendedTags.entrySet());
    sortedRecommendedTags.sort(
        new Comparator<Entry<Tag, Integer>>() {
          @Override
          public int compare(Entry<Tag, Integer> a, Entry<Tag, Integer> b) {
            return a.getValue() > b.getValue() ? -1 : a.getValue() == b.getValue() ? 0 : 1;
          }
        });

    List<Tag> returnedRecommendedTags = new ArrayList<>();
    for (int i = 0; i < resultCount && i < sortedRecommendedTags.size(); i++) {
      returnedRecommendedTags.add(sortedRecommendedTags.get(i).getKey());
    }
    return returnedRecommendedTags;
  }
}
