package Control;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.controlsfx.control.textfield.TextFields;

public class TranslateThis_TuDienPageController implements Initializable, ToggleSwitchListener {

    @FXML
    private ComboBox<String> selectmode;

    @FXML
    private TextField searchtext;

    @FXML
    private Label wordformaterror;

    @FXML
    private Label wordlengtherror;

    @FXML
    private TextFlow displaytext;

    @FXML
    private Button add;

    // Khởi tạo đối tượng function của TranslateThis_Function để gọi hàm
    TranslateThis_Function function = new TranslateThis_Function();

    // Lưu giá trị trạng thái của Toogle Switch
    private boolean check;

    // Kiểm tra lỗi
    boolean error;

    // Đặt giá trị table mặc định khi nếu người dùng không tương tác
    private String table = "AnhViet";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nội dung combobox select mode
        ObservableList<String> mode = FXCollections.observableArrayList("Tiếng Anh sang Tiếng Việt", "Tiếng Anh sang Tiếng Anh");
        selectmode.setItems(mode);
        selectmode.setValue("Tiếng Anh sang Tiếng Việt");

        // Hộp gợi ý
        TextFields.bindAutoCompletion(searchtext, request ->
        {
            String userinput = request.getUserText();
            return function.DisplaySuggestions(userinput);
        });

        // Tắt nút thêm mặc định 
        add.setDisable(true);

    }

    // Lấy kết quả thay đổi của ToggleSwitch lưu vào biến check
    @Override
    public void OnToggleSwitchChanged(boolean status) {
        this.check = status;

        if (check)
        {
            Platform.runLater(() ->
            {
                selectmode.setDisable(true);
                selectmode.setValue("Tiếng Anh sang Tiếng Việt");
            });

            table = "AnhViet";
        } else
        {
            selectmode.setDisable(false);
            add.setDisable(true);
        }
    }

    // Hiển thị định nghĩa của từ trong trường hợp online - hiển thị lại userinput và result
    private void DisplayTranslationOnline(String userinput, String result) {
        Text userinputtext = new Text(userinput + "\n");
        userinputtext.setFont(Font.font("System", FontWeight.BOLD, 28));

        Text definitiontext = new Text("\nĐịnh nghĩa:\n ");
        definitiontext.setFont(Font.font("System", FontWeight.BOLD, 18));

        Text translationtext = new Text("- " + result);
        translationtext.setFont(Font.font("System", 18));

        // Thêm thông tin mới vào trong textflow displaytext
        displaytext.getChildren().addAll(userinputtext, definitiontext, translationtext);
    }

    // Hiển thị định nghĩa của từ trong trường hợp offline - hiển thị pronounce, type, definition, update và updatedefinition của database
    private void DisplayTranslationOffline(String userinput, List<Word> result) {
        Text userinputtext = new Text(userinput + "\n");
        userinputtext.setFont(Font.font("System", FontWeight.BOLD, 28));
        displaytext.getChildren().add(userinputtext);

        // Vòng lặp for each để lấy data từ list results 
        for (Word information : result)
        {
            String pronounce = information.getPronounce();
            String type = information.getType();
            String definition = information.getDefinition();
            String update = information.getUpdate();
            String updatedefinition = information.getUpdateddefinition();

            if (!pronounce.equalsIgnoreCase("null"))
            {
                Text pronouncetext = new Text(pronounce + "\n");
                pronouncetext.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.ITALIC, 18));
                pronouncetext.setFill(Color.GRAY); // Set the color of the text
                displaytext.getChildren().add(pronouncetext);
            }

            if (!type.equalsIgnoreCase("null"))
            {
                Text typeText = new Text(type + "\n");
                typeText.setFont(Font.font("System", FontWeight.BOLD, 18));
                displaytext.getChildren().add(typeText);
            }

            Text definitiontitle = new Text(table.equals("AnhViet") ? "\nĐịnh nghĩa:\n " : "\nDefinition:\n ");
            definitiontitle.setFont(Font.font("System", FontWeight.BOLD, 18));
            displaytext.getChildren().add(definitiontitle);

            Text translationText = new Text("- " + definition + "\n");
            translationText.setFont(Font.font("System", 18));
            displaytext.getChildren().add(translationText);

            if (update != null)
            {
                Text updateText = new Text(table.equals("AnhViet") ? "\n@ Định nghĩa cập nhập bởi Google Translate ngày: " + update + "\n" : "\n@ Definition updated by Google Translate on: " + update + "\n");
                updateText.setFont(Font.font("System", 18));
                updateText.setFill(Color.RED); // Set the color of the text
                displaytext.getChildren().add(updateText);
            }

            if (updatedefinition != null)
            {
                Text updatedDefinitionText = new Text("- " + updatedefinition);
                updatedDefinitionText.setFont(Font.font("System", 18));
                updatedDefinitionText.setFill(Color.RED);
                displaytext.getChildren().add(updatedDefinitionText);
            }
        }

    }

    private void TranslateAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("LỖI");
        alert.setHeaderText("Từ tìm kiếm không có bản dịch");
        ImageView icon = new ImageView("/image/error.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    private void AlertUpdateSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("TRẠNG THÁI");
        alert.setHeaderText("Cập nhập thông tin thành công");
        ImageView icon = new ImageView("/image/success.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    private void AlertUpdateFail() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("TRẠNG THÁI");
        alert.setHeaderText("Cập nhập thông tin thất bại");
        ImageView icon = new ImageView("/image/error.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    private void AlertAddSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("TRẠNG THÁI");
        alert.setHeaderText("Thêm thông tin thành công");
        ImageView icon = new ImageView("/image/success.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    private void AlertAddFail() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("TRẠNG THÁI");
        alert.setHeaderText("Thêm thông tin thất bại");
        ImageView icon = new ImageView("/image/error.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    // Sự kiện người dùng tương tác với bảng chọn từ điển selectmode
    @FXML
    private void ChangeDictionary(ActionEvent event) {
        String selectedMode = selectmode.getValue();
        if ("Tiếng Anh sang Tiếng Việt".equals(selectedMode))
        {
            table = "AnhViet";
        } else if ("Tiếng Anh sang Tiếng Anh".equals(selectedMode))
        {
            table = "AnhAnh";
        }
    }

    // Sự kiện người dùng tương tác với textfield searchtext
    @FXML
    private void SearchText(KeyEvent event) {
        // Xóa thông tin hiển thị trước đó trong Text Flow displaytext
        displaytext.getChildren().clear();

        // Tắt nút thêm khi người dùng nhập từ mới vào - chặn trường hợp cập nhập định nghĩa của từ vựng trước đó vào từ vựng mới
        add.setDisable(true);

        // Kiểm tra định dạng nhập vào
        String userinput = searchtext.getText().trim();

        String pattern = "\\b[a-zA-Z'-]+\\b";

        if (userinput.isEmpty())
        {
            wordformaterror.setVisible(false);
            wordlengtherror.setVisible(false);
        } else if (!userinput.matches(pattern))
        {
            wordformaterror.setVisible(true);
            wordlengtherror.setVisible(false);
            error = wordformaterror.isVisible();
        } else if (userinput.length() > 30)
        {
            wordformaterror.setVisible(false);
            wordlengtherror.setVisible(true);
            error = wordlengtherror.isVisible();
        } else
        {
            wordformaterror.setVisible(false);
            wordlengtherror.setVisible(false);
            error = false; // Reset lai error
        }

    }

    // Sự kiện người dùng tương tác với nút dịch
    @FXML
    private void TranslateThis(ActionEvent event) {
        // Xóa thông tin hiển thị trước đó trong Text Flow displaytext
        displaytext.getChildren().clear();

        String userinput = searchtext.getText().trim().toLowerCase();

        // Trường hợp người dùng không nhập gì
        if (userinput.isEmpty() || error)
        {
            displaytext.getChildren().clear();
            return;
        }

        // Trường hợp người dùng nhập thông tin
        if (check) // Toggle switch dang được chọn
        {
            try
            {
                // Bật nút thêm chỉ khi người dùng tương tác với nút dịch - chặn trường hợp người dùng cập nhập từ vựng không có định nghĩa
                add.setDisable(false);
                String result = function.OnlineTranslate(userinput).toLowerCase();
                DisplayTranslationOnline(userinput, result);
            } catch (Exception e)
            {
                TranslateAlert();
            }
        } else // Toggle switch không được chọn
        {
            List<Word> result = function.OfflineTranslate(table, userinput);

            if (result.isEmpty())
            {
                TranslateAlert();
            } else
            {
                DisplayTranslationOffline(userinput, result);
            }
        }
    }

    // Sự kiện người dùng tương tác với nút thêm - chỉ hoạt động ở chế độ online
    @FXML
    private void Add(ActionEvent event) {
        String userinput = searchtext.getText().trim().toLowerCase();

        // Trường hợp người dùng nhập thông tin
        String result;

        try
        {
            // Lấy kết quả trả về từ OnlineTranslate lưu vào biến result
            result = function.OnlineTranslate(userinput).toLowerCase();
        } catch (IOException ex)
        {
            System.err.println("Error: " + ex.getMessage());
            return;
        }

        // Kiểm tra xem từ vựng được thêm vào đã có trong database hay chưa
        String wordcheck = function.CheckWordInData(table, userinput);

        // Kiểm tra xem từ vựng được thêm vào đã có trong database hay chưa
        String definitioncheck = function.CheckWordDefinitionInData(table, userinput);
        
        String updateddefinitioncheck = function.CheckWordUpdatedDefinitionInData(table, userinput);

        // Trường hợp không tồn tại từ vựng trong database
        if (wordcheck == null)
        {
            Alert addconfirm = new Alert(Alert.AlertType.CONFIRMATION);
            addconfirm.setTitle("LƯU Ý");
            addconfirm.setHeaderText("Bạn có muốn thêm từ này vào danh sách từ vựng");
            ImageView icon = new ImageView("/image/warning.png");
            icon.setFitHeight(48);
            icon.setFitWidth(48);
            addconfirm.getDialogPane().setGraphic(icon);

            Optional<ButtonType> choice = addconfirm.showAndWait();

            if (choice.isPresent() && choice.get() == ButtonType.OK)
            {
                boolean addsuccessful = function.AddWord(table, userinput, result);

                if (addsuccessful)
                {
                    AlertAddSuccess();
                } else
                {
                    AlertAddFail();
                }

            }

        } else if (result.equalsIgnoreCase(definitioncheck) || result.equalsIgnoreCase(updateddefinitioncheck))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("LỖI");
            alert.setHeaderText("Định nghĩa của từ đang tìm kiếm đã có trong danh sách từ vựng");
            ImageView icon = new ImageView("/image/error.png");
            icon.setFitHeight(48);
            icon.setFitWidth(48);
            alert.getDialogPane().setGraphic(icon);
            alert.showAndWait();
        } else // Trường hợp tồn tại từ vựng trong database
        {
            Alert updateconfirm = new Alert(Alert.AlertType.CONFIRMATION);
            updateconfirm.setTitle("LƯU Ý");
            updateconfirm.setHeaderText("Từ đang tìm kiếm đã có trong danh sách từ vựng");
            updateconfirm.setContentText("Bạn có muốn bổ sung thêm định nghĩa vào bản dịch ?");
            ImageView iconUpdate = new ImageView("/image/warning.png");
            iconUpdate.setFitHeight(48);
            iconUpdate.setFitWidth(48);
            updateconfirm.getDialogPane().setGraphic(iconUpdate);

            Optional<ButtonType> choice = updateconfirm.showAndWait();

            if (choice.isPresent() && choice.get() == ButtonType.OK)
            {
                String updatedate = function.DateDisplay();
                boolean updatesuccessful = function.UpdateDefinition(table, userinput, updatedate, result);

                if (updatesuccessful)
                {
                    AlertUpdateSuccess();
                } else
                {
                    AlertUpdateFail();
                }
            }
        }
    }
}
