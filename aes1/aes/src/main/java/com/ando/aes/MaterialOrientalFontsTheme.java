package com.ando.aes;

import mdlaf.themes.MaterialLiteTheme;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

/**
 * 自定义 Material 主题，支持中文字体
 */
public class MaterialOrientalFontsTheme extends MaterialLiteTheme {

    @Override
    protected void installFonts() {
        // 设置支持中文的字体，例如 Microsoft YaHei
        this.fontBold = new FontUIResource("Microsoft YaHei", Font.BOLD, 14);
        this.fontItalic = new FontUIResource("Microsoft YaHei", Font.ITALIC, 14);
        this.fontMedium = new FontUIResource("Microsoft YaHei", Font.PLAIN, 14);
        this.fontRegular = new FontUIResource("Microsoft YaHei", Font.PLAIN, 14);
    }
}