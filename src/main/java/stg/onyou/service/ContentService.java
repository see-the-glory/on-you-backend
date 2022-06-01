package stg.onyou.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stg.onyou.model.entity.Content;
import stg.onyou.repository.ContentRepository;

import java.util.List;

@Service
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public CursorResult<Content> get(Long cursorId, Pageable page) {
        final List<Content> contents = getContents(cursorId, page);
        final Long lastIdOfList = contents.isEmpty() ?
                null : contents.get(contents.size() - 1).getId();

        return new CursorResult<>(contents, hasNext(lastIdOfList));
    }

    private List<Content> getContents(Long id, Pageable page) {
        return id == null ?
                this.contentRepository.findAllByOrderByIdDesc(page) :
                this.contentRepository.findByIdLessThanOrderByIdDesc(id, page);
    }

    private Boolean hasNext(Long id) {
        if (id == null) return false;
        return this.contentRepository.existsByIdLessThan(id);
    }
}