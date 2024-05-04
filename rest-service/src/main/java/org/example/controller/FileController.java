package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.entity.AppDocument;
import org.example.entity.BinaryContent;
import org.example.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Log4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @GetMapping("/get-doc")
    public ResponseEntity<?> getDocument(@RequestParam("id") String id) {
        //TODO для формирования badRequest добавить ControllerAdvice
        AppDocument document = fileService.getDocument(id);
        if (document == null) {
            return ResponseEntity.badRequest().build();
        }
        BinaryContent binaryContent = document.getBinaryContent();
        var fileSystemResource = binaryContent.getFileAsArrayOfBytes();
        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        }
// В header Content-disposition указывает клиентскому приложению, например, браузеру, как именно воспринимать полученную инфу
// тут задаем attachment, чтобы бразуер скачал полученный файл, если этот хедер не добавить, то изображение или документ откроются
// сразу в окне браузера, без скачивания
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .header("Content-disposition", "attachment; filename=" + document.getDocName())
                .body(fileSystemResource);
    }

    @GetMapping("/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
        //TODO для формирования badRequest добавить ControllerAdvice
        var photo = fileService.getPhoto(id);
        if (photo == null) {
            return ResponseEntity.badRequest().build();
        }
        BinaryContent binaryContent = photo.getBinaryContent();
        var fileSystemResource = binaryContent.getFileAsArrayOfBytes();
        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResource);
    }
}