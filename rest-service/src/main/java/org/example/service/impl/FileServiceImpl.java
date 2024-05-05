package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.dao.AppDocumentDAO;
import org.example.dao.AppPhotoDAO;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.service.FileService;
import org.example.utils.CryptoTool;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;
    @Override
    public AppDocument getDocument(String hash) {
        var docId = cryptoTool.idOf(hash);
        if(docId == null){
            return null;
        }
        return appDocumentDAO.findById(docId).orElseThrow(
                ()->new RuntimeException("Document with id: "+docId+" not found.")
        );
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var photoId = cryptoTool.idOf(hash);
        if(photoId == null){
            return null;
        }
        return appPhotoDAO.findById(photoId).orElseThrow(
                ()->new RuntimeException("Photo with id: "+photoId+" not found.")
        );
    }

}
