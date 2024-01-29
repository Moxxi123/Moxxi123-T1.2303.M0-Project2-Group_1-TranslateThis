package Control;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class TranslateThis_DanhSachTuEditPageController implements Initializable {

    @FXML
    private TextField word;

    @FXML
    private TextField pronounce;

    @FXML
    private ComboBox<String> type;

    @FXML
    private TextArea definition;

    @FXML
    private DatePicker date;

    @FXML
    private TextArea newdefinition;

    @FXML
    private Label wordemptyerror;

    @FXML
    private Label wordformaterror;

    @FXML
    private Label wordlengtherror;

    @FXML
    private Label wordduplicateerror;

    @FXML
    private Label pronounceemptyerror;

    @FXML
    private Label prononceformaterror;

    @FXML
    private Label definitionemptyerror;

    @FXML
    private Button AcceptButton;

    @FXML
    private Button CancelButton;

    // Khởi tạo đối tượng TranslateThis_Function để gọi hàm
    TranslateThis_Function function = new TranslateThis_Function();

    // Tạo biến table để nhận giá trị tablechoice được truyền qua
    private String table;

    // Tạo List để lưu trữ thông tin từ hàm UpdateWordInformation
    private List<Word> result;

    // Biến oldinput để lưu giá trị cũ của word để so sánh
    private String oldinput;

    // Biến để lưu các giá trị nhập vào các trường để thực hiện câu lệnh update
    private Integer updateid;
    private String updateword;
    private String updatepronounce;
    private String updatetype;
    private String updatedefinition;
    private String updatedate;
    private String updatenewdefinition;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Bảng chọn type
        ObservableList<String> typeoption = FXCollections.observableArrayList("noun", "verb", "adjective", "adverb");
        type.setItems(typeoption);
        type.setValue("noun");

    }

    // Lấy giá trị của tablechoice ở trang DanhSachTuPageController truyền qua
    public void SetTableChoice(String tablechoice) {
        this.table = tablechoice;

        // Lấy thông tin từ hàm UpdateWordInformation
        result = function.GetWordForUpdate(table);

        // Tạo đối tượng word của lớp Word để chiết xuất thông tin
        Word wordinformation = result.get(0);

        // Lấy thông tin từ wordinformation nhét vào các trường tương ứng
        // Phần không null
        updateid = wordinformation.getId();

        word.setText(wordinformation.getWord());
        // Lưu giá trị lấy từ database vào biến để so sánh khi người dùng nhập vào
        oldinput = word.getText().trim();
        // Lưu giá trị lấy từ database vào biến để cập nhập trong trường hợp người dùng không tương tác
        updateword = word.getText().trim();

        definition.setText(wordinformation.getDefinition());
        // Lưu giá trị lấy từ database vào biến để cập nhập trong trường hợp người dùng không tương tác
        updatedefinition = definition.getText().trim();

        // Phần có thể null
        String pronouncetext = wordinformation.getPronounce();
        if (pronouncetext.equalsIgnoreCase("null")) // Kiểm tra pronounce có giá trị null hay không
        {
            pronounceemptyerror.setVisible(true); // Có thì bắt lỗi ẩn nút Accept
            prononceformaterror.setVisible(false);
            DisableAcceptButton();
        } else
        {
            pronounce.setText(pronouncetext);
            // Lưu giá trị lấy từ database vào biến để cập nhập trong trường hợp người dùng không tương tác
            updatepronounce = pronounce.getText().trim();
        }

        String typevalue = wordinformation.getType();
        if (typevalue.equalsIgnoreCase("null")) // Kiểm tra type có giá trị null hay không
        {
            type.setValue("Noun"); // Có thì set giá trị mặc định là "Noun"
        } else
        {
            type.setValue(typevalue);
            // Lưu giá trị lấy từ database vào biến để cập nhập trong trường hợp người dùng không tương tác
            updatetype = type.getValue();
        }

        // Chuyển sang định dạng LocalDate để bỏ vào DatePicker
        String dateformat = wordinformation.getUpdate();

        if (dateformat != null && !dateformat.isEmpty())
        {
            LocalDate localdate = LocalDate.parse(dateformat, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            date.setValue(localdate);
            // Lưu giá trị lấy từ database vào biến để cập nhập trong trường hợp người dùng không tương tác
            updatedate = dateformat;
        } else
        {
            date.setValue(null);
            updatedate = null;
        }

        String newdefinitiontext = wordinformation.getUpdateddefinition();

        if (newdefinitiontext != null && !newdefinitiontext.isEmpty())
        {
            newdefinition.setText(newdefinitiontext);
            // Lưu giá trị lấy từ database vào biến để cập nhập trong trường hợp người dùng không tương tác
            updatenewdefinition = newdefinitiontext.trim();
        } else
        {
            updatenewdefinition = null;
        }
    }

    // Ẩn nút OK khi có thông báo lỗi hiển thị
    private void DisableAcceptButton() {
        boolean error = wordemptyerror.isVisible() || wordformaterror.isVisible() || wordlengtherror.isVisible() || wordduplicateerror.isVisible() || pronounceemptyerror.isVisible() || prononceformaterror.isVisible() || definitionemptyerror.isVisible();

        AcceptButton.setDisable(error);
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

    // Sự kiện nhập vào TextField word
    @FXML
    private void WordUpdate(KeyEvent event) {
        String newuserinput = word.getText().trim().toLowerCase();

        String pattern = "\\b[a-zA-Z'-]+\\b";

        // Kiếm tra xem nếu người dùng nhập vào giá trị giống với giá trị cũ thì cho qua không kiểm tra
        if (newuserinput.equals(oldinput))
        {
            wordemptyerror.setVisible(false);
            wordduplicateerror.setVisible(false);
            updateword = newuserinput;
        } else
        {
            // Gọi hàm CheckWordInData để kiểm tra giá trị nhập vào có trùng hay không
            String check = function.CheckWordInData(table, newuserinput);

            // Kiểm tra giá trị nhập vào có để trống
            if (newuserinput.isEmpty())
            {
                wordemptyerror.setVisible(true);
                wordformaterror.setVisible(false);
                wordlengtherror.setVisible(false);
                wordduplicateerror.setVisible(false);
            } else if (!newuserinput.matches(pattern))
            {
                wordemptyerror.setVisible(false);
                wordformaterror.setVisible(true);
                wordlengtherror.setVisible(false);
                wordduplicateerror.setVisible(false);
            } else if (newuserinput.length() > 30)
            {
                wordemptyerror.setVisible(false);
                wordformaterror.setVisible(false);
                wordlengtherror.setVisible(true);
                wordduplicateerror.setVisible(false);
            } else if (check != null && !check.isEmpty()) //Kiểm tra giá trị nhập vào có trùng hay không
            {
                wordemptyerror.setVisible(false);
                wordformaterror.setVisible(false);
                wordlengtherror.setVisible(false);
                wordduplicateerror.setVisible(true);
            } else
            {
                wordemptyerror.setVisible(false);
                wordformaterror.setVisible(false);
                wordlengtherror.setVisible(false);
                wordduplicateerror.setVisible(false);
                updateword = newuserinput;
            }
        }

        DisableAcceptButton();
    }

    // Sự kiện nhập vào TextField pronounce
    @FXML
    private void PronounceUpdate(KeyEvent event) {
        String userinput = pronounce.getText().trim().toLowerCase();

        String pattern = "^/[^/]+/$";

        if (userinput.isEmpty())
        {
            pronounceemptyerror.setVisible(true);
            prononceformaterror.setVisible(false);
        } else if (!userinput.matches(pattern))
        {
            pronounceemptyerror.setVisible(false);
            prononceformaterror.setVisible(true);
        } else
        {
            pronounceemptyerror.setVisible(false);
            prononceformaterror.setVisible(false);
            updatepronounce = userinput;
        }

        DisableAcceptButton();
    }

    // Sự kiện nhấn vào ComboBox type
    @FXML
    private void TypeUpdate(ActionEvent event) {
        updatetype = type.getValue();
    }

    // Sự kiện nhập vào TextArea definition
    @FXML
    private void DefinitionUpdate(KeyEvent event) {
        String userinput = definition.getText().trim().toLowerCase();

        if (userinput.isEmpty())
        {
            definitionemptyerror.setVisible(true);
        } else
        {
            definitionemptyerror.setVisible(false);
            updatedefinition = userinput;

        }

        DisableAcceptButton();
    }

    @FXML
    private void DateUpdate(ActionEvent event) {
        LocalDate localdate = date.getValue();
        if (localdate != null)
        {
            updatedate = localdate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else
        {
            updatedate = null;
        }
    }

    // Sự kiện nhập vào TextArea newdefinition
    @FXML
    private void NewDefinitionUpdate(KeyEvent event) {
        String userinput = newdefinition.getText().trim().toLowerCase();

        if (userinput.isEmpty())
        {
            updatenewdefinition = null;
        } else
        {
            updatenewdefinition = userinput;

        }
    }

    @FXML
    private void Update(MouseEvent event) {
//        System.out.println("Update Word: " + updateword);
//        System.out.println("Update Pronounce: " + updatepronounce);
//        System.out.println("Update Type: " + updatetype);
//        System.out.println("Update Definition: " + updatedefinition);
//        System.out.println("Update Date: " + updatedate);
//        System.out.println("Update New Definition: " + updatenewdefinition);

        boolean updatesuccessful = function.UpdateWordInformation(table, updateword, updatepronounce, updatetype, updatedefinition, updatedate, updatenewdefinition, updateid);

        if (updatesuccessful)
        {
            AlertUpdateSuccess();
        } else
        {
            AlertUpdateFail();
        }

    }

    @FXML
    private void Cancel(MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
