package com.ems.employee_management.service;

import com.ems.employee_management.entity.User;
import com.ems.employee_management.entity.enums.Role;
import com.ems.employee_management.entity.enums.Status;
import com.ems.employee_management.exception.BadRequestException;
import com.ems.employee_management.repository.UserRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryExportService {

    private final UserRepository userRepository;

    public byte[] exportExcel(Role viewRole, String keyword, String location, Integer minAge, Integer maxAge,
            String sortBy, String direction) throws IOException {
        List<User> users = getUsersForView(viewRole, keyword, location, minAge, maxAge, sortBy, direction);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Directory");
            String[] headers = { "Employee ID", "Name", "Username", "Email", "Contact", "Location", "Role", "Status" };
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNumber = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNumber++);
                row.createCell(0).setCellValue(user.getEmployeeId());
                row.createCell(1).setCellValue(user.getFirstName() + " " + user.getLastName());
                row.createCell(2).setCellValue(user.getUsername());
                row.createCell(3).setCellValue(user.getEmail());
                row.createCell(4).setCellValue(user.getContactNumber());
                row.createCell(5).setCellValue(nullSafe(user.getLocation()));
                row.createCell(6).setCellValue(user.getRole().name());
                row.createCell(7).setCellValue(user.getStatus().name());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(output);
            return output.toByteArray();
        }
    }

    public byte[] exportPdf(Role viewRole, String keyword, String location, Integer minAge, Integer maxAge,
            String sortBy, String direction) throws IOException, DocumentException {
        List<User> users = getUsersForView(viewRole, keyword, location, minAge, maxAge, sortBy, direction);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();
            document.add(new Paragraph("Employee Directory"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            addHeader(table, "Employee ID");
            addHeader(table, "Name");
            addHeader(table, "Email");
            addHeader(table, "Location");
            addHeader(table, "Role");

            for (User user : users) {
                table.addCell(user.getEmployeeId());
                table.addCell(user.getFirstName() + " " + user.getLastName());
                table.addCell(user.getEmail());
                table.addCell(nullSafe(user.getLocation()));
                table.addCell(user.getRole().name());
            }

            document.add(table);
            document.close();
            return output.toByteArray();
        }
    }

    private List<User> getUsersForView(Role viewRole, String keyword, String location, Integer minAge, Integer maxAge,
            String sortBy, String direction) {
        if (currentRole() != viewRole) {
            throw new BadRequestException("Your role is wrong");
        }

        Comparator<User> comparator = comparator(sortBy);
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return userRepository.findAll().stream()
                .filter(user -> visibleTo(viewRole, user))
                .filter(user -> matchesKeyword(user, keyword))
                .filter(user -> matchesLocation(user, location))
                .filter(user -> matchesAge(user, minAge, maxAge))
                .sorted(comparator)
                .toList();
    }

    private boolean visibleTo(Role viewRole, User user) {
        if (viewRole == Role.ADMIN) {
            return user.getRole() != Role.ADMIN;
        }

        if (viewRole == Role.MANAGER) {
            return user.getStatus() == Status.APPROVED && user.getRole() == Role.EMPLOYEE;
        }

        return user.getStatus() == Status.APPROVED && user.getRole() != Role.ADMIN;
    }

    private boolean matchesKeyword(User user, String keyword) {
        String cleanKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        if (cleanKeyword.isBlank()) {
            return true;
        }

        return contains(user.getEmployeeId(), cleanKeyword)
                || contains(user.getFirstName(), cleanKeyword)
                || contains(user.getLastName(), cleanKeyword)
                || contains(user.getUsername(), cleanKeyword)
                || contains(user.getEmail(), cleanKeyword);
    }

    private boolean matchesLocation(User user, String location) {
        String cleanLocation = location == null ? "" : location.trim();
        return cleanLocation.isBlank() || cleanLocation.equalsIgnoreCase(user.getLocation());
    }

    private boolean matchesAge(User user, Integer minAge, Integer maxAge) {
        if (minAge == null || maxAge == null) {
            return true;
        }

        if (user.getDob() == null) {
            return false;
        }

        int age = Period.between(user.getDob(), LocalDate.now()).getYears();
        return age >= minAge && age <= maxAge;
    }

    private Comparator<User> comparator(String sortBy) {
        return switch (sortBy == null ? "firstName" : sortBy) {
            case "employeeId" -> Comparator.comparing(User::getEmployeeId, String.CASE_INSENSITIVE_ORDER);
            case "email" -> Comparator.comparing(User::getEmail, String.CASE_INSENSITIVE_ORDER);
            case "location" -> Comparator.comparing(user -> nullSafe(user.getLocation()), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(user -> nullSafe(user.getFirstName()), String.CASE_INSENSITIVE_ORDER);
        };
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private void addHeader(PdfPTable table, String text) {
        table.addCell(new PdfPCell(new Phrase(text)));
    }

    private Role currentRole() {
        String authority = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Unauthorized"))
                .getAuthority();

        return Role.valueOf(authority.replace("ROLE_", ""));
    }
}
