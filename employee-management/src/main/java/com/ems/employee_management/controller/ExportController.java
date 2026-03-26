package com.ems.employee_management.controller;

import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.service.DirectoryExportService;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/exports")
@RequiredArgsConstructor
public class ExportController {

    private final DirectoryExportService directoryExportService;

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam Role viewRole,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) throws IOException {

        byte[] file = directoryExportService.exportExcel(viewRole, keyword, location, minAge, maxAge, sortBy, direction);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=directory.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam Role viewRole,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) throws IOException, DocumentException {

        byte[] file = directoryExportService.exportPdf(viewRole, keyword, location, minAge, maxAge, sortBy, direction);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=directory.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}
