package Control;

import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class TranslateThis_DanhSachTuAddPageController implements Initializable {

    @FXML
    private TextField word;

    @FXML
    private TextField pronounce;

    @FXML
    private ComboBox<String> type;

    @FXML
    private TextArea definition;

    @FXML
    private Label wordemptyerror;
    
    @FXML
    private Label wordlengtherror;
    
    @FXML
    private Label wordformaterror;

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

    // Tạo List để lưu trữ thông tin từ hàm AddWordInformation
    private List<Word> result;


    // trường khai báo từ cac biến để lưu giá trị từ bảng (table)
    private String addword;
    private String addpronounce;
    private String addtype;
    private String adddefinition;
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        DisableAcceptButton();

        // Bảng chọn type
        ObservableList<String> typeoption = FXCollections.observableArrayList("noun", "verb", "adjective", "adverb");
        type.setItems(typeoption);
        type.setValue("noun");
        addtype = "noun";

    }

    // Lấy giá trị của tablechoice ở trang DanhSachTuPageController truyền qua
    public void SetTableChoice(String tablechoice) {
        this.table = tablechoice;
    }

    //Hàm Ẩn nút OK khi có thông báo lỗi hiển thị
    private void DisableAcceptButton() {
        boolean error = wordemptyerror.isVisible() || wordformaterror.isVisible() || wordlengtherror.isVisible() || wordduplicateerror.isVisible() || pronounceemptyerror.isVisible() || prononceformaterror.isVisible() || definitionemptyerror.isVisible();
        
        boolean empty = word.getText().trim().isEmpty() || pronounce.getText().trim().isEmpty() || definition.getText().trim().isEmpty();

        AcceptButton.setDisable(error || empty);
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

    // Sự kiện nhập vào TextField word
    @FXML
    private void WordAdd(KeyEvent event) {
        String userinput = word.getText().trim().toLowerCase();
        
        String pattern = "\\b[a-zA-Z'-]+\\b"; // chấp nhận chuxw hoa hoặc chữ thường, dấu gạch ngang

        // Gọi hàm CheckWordInData để kiểm tra giá trị nhập vào có trùng hay không
        String check = function.CheckWordInData(table, userinput);

        // Kiểm tra giá trị nhập vào có để trống
        if (userinput.isEmpty())
        {
            wordemptyerror.setVisible(true);
            wordformaterror.setVisible(false);
            wordlengtherror.setVisible(false);
            wordduplicateerror.setVisible(false);
        } else if(!userinput.matches(pattern)){ // Kiểm tra từ nhập vào có đúng định dạng từ Tiếng Anh hay không
            wordemptyerror.setVisible(false);
            wordformaterror.setVisible(true);
            wordlengtherror.setVisible(false);
            wordduplicateerror.setVisible(false);
        } else if(userinput.length() > 30){ // Kiểm tra độ dài ký tự (giới hạn 30tu)
            wordemptyerror.setVisible(false);
            wordformaterror.setVisible(false);
            wordlengtherror.setVisible(true);
            wordduplicateerror.setVisible(false);
        }
        else if (check != null && !check.isEmpty()) //Kiểm tra giá trị nhập vào có trùng hay không
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
            addword = userinput;
        }

        DisableAcceptButton();
    }

// Sự kiện nhập vào TextField pronounce
    @FXML
    private void PronounceAdd(KeyEvent event) {
        String userinput = pronounce.getText().trim().toLowerCase();

        String pattern = "^/[^/]+/$";  // ràng buộc pattern bắt đầu kết thúc "//" bất kì ký tự, trừ "/"

        if (userinput.isEmpty())
        {
            pronounceemptyerror.setVisible(true);
            prononceformaterror.setVisible(false);
        } else if (!userinput.matches(pattern)) // khác pattern, báo lỗi định dạng
        {
            pronounceemptyerror.setVisible(false);
            prononceformaterror.setVisible(true);
        } else
        {
            pronounceemptyerror.setVisible(false);
            prononceformaterror.setVisible(false);
            addpronounce = userinput;
        }

        DisableAcceptButton(); // gọi hàm để kiểm tra
    }

    // Sự kiện nhấn vào ComboBox type
    @FXML
    private void TypeAdd(ActionEvent event) {
        addtype = type.getValue();
    }

    // Sự kiện nhập vào TextArea definition
    @FXML
    private void DefinitionAdd(KeyEvent event) {
        String userinput = definition.getText().trim().toLowerCase();

        if (userinput.isEmpty())
        {
            definitionemptyerror.setVisible(true);
        } else
        {
            definitionemptyerror.setVisible(false);
            adddefinition = userinput;

        }

        DisableAcceptButton();
    }

    @FXML
    private void Add(MouseEvent event) {

        boolean addsuccessful = function.AddWordInformation(table, addword, addpronounce, addtype, adddefinition);

        if (addsuccessful)
        {
            AlertAddSuccess(); //true
        } else
        {
            AlertAddFail(); //false
        }

    }

    @FXML
    private void Cancel(MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
