package com.liteThinking.products.service;

import com.liteThinking.products.entity.Empresa;
import com.liteThinking.products.entity.Producto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class PdfService {

    private static final float MARGIN = 50;
    private static final float[] COL_WIDTHS = {30, 100, 100, 65, 65, 65, 70};
    private static final float ROW_HEIGHT = 20;
    private static final float HEADER_HEIGHT = 25;

    private final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private final double tasaUsd;
    private final double tasaEur;

    public PdfService(@Value("${app.tasa.cop-usd}") double tasaUsd,
                      @Value("${app.tasa.cop-eur}") double tasaEur) {
        this.tasaUsd = tasaUsd;
        this.tasaEur = tasaEur;
    }

    public byte[] generarInventario(Empresa empresa, List<Producto> productos) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float y = page.getMediaBox().getHeight() - MARGIN;
                y = dibujarEncabezado(cs, empresa, y);
                y -= 10;
                dibujarTabla(cs, y, productos);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private float dibujarEncabezado(PDPageContentStream cs, Empresa empresa, float y) throws IOException {
        cs.setFont(boldFont, 18);
        cs.beginText();
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("Inventario");
        cs.endText();

        y -= 25;
        cs.setFont(font, 11);
        cs.beginText();
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("Empresa: " + empresa.getNombre() + "  |  NIT: " + empresa.getNit());
        cs.endText();

        y -= 16;
        cs.beginText();
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        cs.endText();

        return y;
    }

    private void dibujarTabla(PDPageContentStream cs, float y, List<Producto> productos) throws IOException {
        y -= 10;

        String[] headers = {"ID", "Nombre", "Caracteristicas", "COP", "USD", "EUR", "Categoria"};
        float x = MARGIN;

        cs.setFont(boldFont, 9);
        cs.setLineWidth(1);

        for (int i = 0; i < headers.length; i++) {
            cs.addRect(x, y - HEADER_HEIGHT, COL_WIDTHS[i], HEADER_HEIGHT);
            x += COL_WIDTHS[i];
        }
        cs.stroke();

        x = MARGIN;
        float textY = y - 17;
        for (int i = 0; i < headers.length; i++) {
            cs.beginText();
            cs.newLineAtOffset(x + 2, textY);
            cs.showText(headers[i]);
            cs.endText();
            x += COL_WIDTHS[i];
        }

        y -= HEADER_HEIGHT;
        NumberFormat fmtCop = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        NumberFormat fmtUsd = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat fmtEur = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        for (Producto p : productos) {
            BigDecimal cop = p.getPrecio();
            BigDecimal usd = cop.multiply(BigDecimal.valueOf(tasaUsd)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal eur = cop.multiply(BigDecimal.valueOf(tasaEur)).setScale(2, RoundingMode.HALF_UP);

            String[] row = {
                String.valueOf(p.getProductoId()),
                p.getNombreProducto(),
                p.getCaracteristicas() != null ? p.getCaracteristicas() : "",
                fmtCop.format(cop),
                fmtUsd.format(usd),
                fmtEur.format(eur),
                p.getCategoria() != null ? p.getCategoria().getNombreCategoria() : ""
            };

            y -= ROW_HEIGHT;
            if (y < MARGIN) return;

            x = MARGIN;
            cs.setFont(font, 8);
            for (int i = 0; i < row.length; i++) {
                cs.addRect(x, y, COL_WIDTHS[i], ROW_HEIGHT);
                x += COL_WIDTHS[i];
            }
            cs.stroke();

            x = MARGIN;
            float rowTextY = y + 6;
            for (int i = 0; i < row.length; i++) {
                String text = row[i];
                if (text.length() > 18 && i == 2) {
                    text = text.substring(0, 17) + "..";
                }
                cs.beginText();
                cs.newLineAtOffset(x + 2, rowTextY);
                cs.showText(text);
                cs.endText();
                x += COL_WIDTHS[i];
            }
        }
    }
}
