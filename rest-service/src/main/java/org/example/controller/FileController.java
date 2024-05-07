package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.entity.AppDocument;
import org.example.entity.BinaryContent;
import org.example.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @GetMapping("/get-doc")
    public void getDocument(@RequestParam("id") String id, HttpServletResponse response) {
        //TODO для формирования badRequest добавить ControllerAdvice
        AppDocument document = fileService.getDocument(id);
        if (document == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // В header Content-disposition указывает клиентскому приложению, например, браузеру, как именно воспринимать полученную инфу
// тут задаем attachment, чтобы бразуер скачал полученный файл, если этот хедер не добавить, то изображение или документ откроются
// сразу в окне браузера, без скачивания
        response.setContentType(MediaType.parseMediaType(document.getMimeType()).toString());
        response.setHeader("Content-disposition", "attachment; filename=" + document.getDocName());
        response.setStatus(HttpServletResponse.SC_OK);

        BinaryContent binaryContent = document.getBinaryContent();
        try {
            var output = response.getOutputStream();
            output.write(binaryContent.getFileAsArrayOfBytes());
            output.close();
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response) {
        //TODO для формирования badRequest добавить ControllerAdvice
        var photo = fileService.getPhoto(id);
        if (photo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-disposition","attachment;");
        response.setStatus(HttpServletResponse.SC_OK);

        BinaryContent binaryContent = photo.getBinaryContent();

       try{
           var output = response.getOutputStream();
           output.write(binaryContent.getFileAsArrayOfBytes());
           output.close();
       }catch (IOException e){
           log.error(e);
           response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
       }
    }
}