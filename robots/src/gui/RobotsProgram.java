package gui;

import java.awt.Frame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram
{
    public static void main(String[] args) {
        setRussianUIManagerText();
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame();
            frame.pack();
            frame.setVisible(true);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        });
    }

    protected static void setRussianUIManagerText() {
        // JOptionPane
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.titleText", "Выберите опцию");

        // JFileChooser
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.openButtonToolTipText", "Открыть выбранный файл");
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить выбранный файл");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Отменить выбор");
        UIManager.put("FileChooser.directoryOpenButtonText", "Открыть");
        UIManager.put("FileChooser.directoryOpenButtonToolTipText", "Открыть выбранную папку");

        UIManager.put("FileChooser.lookInLabelText", "Папка:");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов:");
        UIManager.put("FileChooser.upFolderToolTipText", "На уровень вверх");
        UIManager.put("FileChooser.homeFolderToolTipText", "Домашняя папка");
        UIManager.put("FileChooser.newFolderToolTipText", "Создать новую папку");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
        UIManager.put("FileChooser.fileNameHeaderText", "Имя");
        UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
        UIManager.put("FileChooser.fileTypeHeaderText", "Тип");
        UIManager.put("FileChooser.fileDateHeaderText", "Изменен");
        UIManager.put("FileChooser.fileAttrHeaderText", "Атрибуты");

        UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
        UIManager.put("FileChooser.newFolderDialogTitle", "Новая папка");
        UIManager.put("FileChooser.newFolderPromptText", "Имя новой папки:");

        // JColorChooser
        UIManager.put("ColorChooser.previewText", "Предпросмотр");
        UIManager.put("ColorChooser.okText", "OK");
        UIManager.put("ColorChooser.cancelText", "Отмена");
        UIManager.put("ColorChooser.resetText", "Сброс");
        UIManager.put("ColorChooser.sampleText", "Образец текста");

        UIManager.put("ColorChooser.swatchesNameText", "Образцы");
        UIManager.put("ColorChooser.swatchesRecentText", "Последние:");
        UIManager.put("ColorChooser.hsvNameText", "HSV");
        UIManager.put("ColorChooser.hslNameText", "HSL");
        UIManager.put("ColorChooser.rgbNameText", "RGB");
        UIManager.put("ColorChooser.cmykNameText", "CMYK");

        UIManager.put("ColorChooser.hueText", "Оттенок");
        UIManager.put("ColorChooser.saturationText", "Насыщенность");
        UIManager.put("ColorChooser.valueText", "Значение");
        UIManager.put("ColorChooser.lightnessText", "Освещенность");
        UIManager.put("ColorChooser.redText", "Красный");
        UIManager.put("ColorChooser.greenText", "Зеленый");
        UIManager.put("ColorChooser.blueText", "Синий");
        UIManager.put("ColorChooser.cyanText", "Голубой");
        UIManager.put("ColorChooser.magentaText", "Пурпурный");
        UIManager.put("ColorChooser.yellowText", "Желтый");
        UIManager.put("ColorChooser.blackText", "Черный");

        // JOptionPane (дополнительные настройки)
        UIManager.put("OptionPane.inputDialogTitle", "Ввод данных");
        UIManager.put("OptionPane.messageDialogTitle", "Сообщение");
        UIManager.put("OptionPane.titleText", "Выберите опцию");
    }
}
