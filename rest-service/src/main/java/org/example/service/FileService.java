package org.example.service;

import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    //нужен для преобразования массива байт в объект FileSystemResource, который
    // необходим для передачи контента в теле http ответа
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
