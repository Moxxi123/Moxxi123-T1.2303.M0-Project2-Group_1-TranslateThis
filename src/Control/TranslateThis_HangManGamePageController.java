package Control;

import javafx.scene.paint.Color;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class TranslateThis_HangManGamePageController implements Initializable {

    @FXML
    private VBox HangmanCODEGAME;

    @FXML
    private VBox HangmanTONGKET;

    @FXML
    private VBox HangmanXEPHANG;

    @FXML
    private VBox HangmanDIEM;

    @FXML
    private HBox dapan;

    @FXML
    private HBox luocChoi;

    @FXML
    private TextFlow cauhoi;

    @FXML
    private TextFlow HT_tongDiem;

    @FXML
    private FlowPane cautraloi;

    @FXML
    private Label sotrang;

    @FXML
    private Text wordText;

    @FXML
    private ImageView hinh_luoc_choi;

    @FXML
    private TableView<TongKetResult> tableTK;

    @FXML
    private TableColumn<TongKetResult, Integer> TKcol_STT;

    @FXML
    private TableColumn<TongKetResult, String> TKcol_CAUHOI;

    @FXML
    private TableColumn<TongKetResult, String> TKcol_DAPAN;

    @FXML
    private TableColumn<TongKetResult, Integer> TKcol_DIEM;

    @FXML
    private TableView<bangxepHang> tableBXH;

    @FXML
    private TableColumn<bangxepHang, Integer> BXHcol_DIEM;

    @FXML
    private TableColumn<bangxepHang, String> BXHcol_NGAY;

    @FXML
    private TableView<bangxepHang> tableBDIEM;

    @FXML
    private TableColumn<bangxepHang, Integer> BDcol_DIEM;

    @FXML
    private TableColumn<bangxepHang, String> BDcol_NGAY;

    @FXML
    private TableColumn<bangxepHang, String> BDcol_TIME;

    private final int maxIterations = 20;

    private int page = 1;

    private int luoc_choi = 3;

    private int diem_BD = 5;
    
    private int err_tang = 0;

    private int tongDiem;

    private String definition = null;

    private String word = null;

    private boolean check_True = false;

    private final int[] Luu_diem_BD = new int[maxIterations];

    private ObservableList<TongKetResult> danhSachTongKet = FXCollections.observableArrayList();

    TranslateThis_Function function = new TranslateThis_Function();

    private Iterator<Map.Entry<String, String>> iterator;

    private LinkedHashMap<String, String> wordDefinitions;

    public String getDefinition(String word) {
        return wordDefinitions.get(word);
    }

    private void clear_DanhSachTongKet() {
        danhSachTongKet.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try
        {
            wordDefinitions = function.Select_WD_Database();

            if (wordDefinitions.isEmpty()) //kiểm tra wordDefinitions có empty hay không trong trường hợp dữ liệu không có từ vựng hoặc không đủ 20 từ vựng
            { 
                wordDefinitions = function.Select_WD_DatabaseBackKup();
            }
        } catch (SQLException ex)
        {
            Logger.getLogger(TranslateThis_HangManGamePageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        iterator = wordDefinitions.entrySet().iterator();

        Alert_BatDau();
        DisplayNextWord();
    }

    @FXML // nút ở trang game
    private void nutTIEPTUC(ActionEvent event) {
        if (check_True)
        {
            resetGame();
            check_True = false;
        } else
        {
            sai3Lan();
            resetGame();
        }
    }

    @FXML // nút trang tổng kết
    private void quaylaiGAME(ActionEvent event) {
        HangmanTONGKET.setVisible(false);
        HangmanCODEGAME.setVisible(true);
        page = 1;
        sotrang.setText(String.valueOf(page));
        try
        {
            wordDefinitions = function.Select_WD_Database();
        } catch (SQLException ex)
        {
            Logger.getLogger(TranslateThis_HangManGamePageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        iterator = wordDefinitions.entrySet().iterator();
        DisplayNextWord();
        resetGiatri();
        capNhapDiemBD();
        clear_DanhSachTongKet();
    }

    @FXML // nút trang tổng kết
    private void toitrangBXH(ActionEvent event) {
        HangmanTONGKET.setVisible(false);
        HangmanXEPHANG.setVisible(true);
        Display_BXH();
    }

    @FXML // nút trang tổng kết
    private void toitrangDIEM(ActionEvent event) {
        HangmanTONGKET.setVisible(false);
        HangmanDIEM.setVisible(true);
        Display_BDIEM();
    }

    @FXML // nút trang Bảng xếp hạng
    private void BXHdenTK(ActionEvent event) {
        HangmanXEPHANG.setVisible(false);
        HangmanTONGKET.setVisible(true);
        Display_TongKet();
    }

    @FXML // nút trang bảng điểm
    private void BDdenTK(ActionEvent event) {
        HangmanDIEM.setVisible(false);
        HangmanTONGKET.setVisible(true);
        Display_TongKet();
    }

    private void DisplayNextWord() {
        if (iterator.hasNext())
        {
            Map.Entry<String, String> entry = iterator.next();
            word = entry.getKey();
            definition = entry.getValue();

            System.out.println(word);

            cauhoi.getChildren().clear();

            wordText = new Text("Từ của định nghĩa \n'" + definition + "' là gì ?");

            wordText.setFont(Font.font("tahoma", FontWeight.BOLD, 25));

            wordText.setFill(Color.PURPLE);

            cauhoi.getChildren().addAll(wordText); // đưa wordText TextFlow vào xuất câu hỏi ra

            updateImageViews();//hiển thị lược chơi

            dapan.getChildren().clear();

            char[] kytudapan = word.toCharArray();// Tách từng ký tự của word lưu vào mảng kytudapan
            char[] star = word.toCharArray();

            for (int i = 0; i < star.length; i++)
            {// Vòng lặp lấy số lượng phần tử có trong star bỏ vào từng label
                star[i] = '?';// Chuyển đổi từng ký tự sang dạng ?
                Label kytu = new Label(String.valueOf(star[i])); // tạo Label có tên là kytu rồi gán  star[i] vào nó

                // Định dạng hiển thị cho kytu (phần đáp án)
                kytu.setFont(Font.font("System", FontWeight.BOLD, 40));
                kytu.setTextFill(Color.BLUE);
                kytu.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                kytu.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
                kytu.setPrefSize(60, 60);
                kytu.setAlignment(Pos.CENTER);
                HBox.setMargin(kytu, new Insets(10, 10, 10, 10));

                dapan.getChildren().add(kytu); // đưa kytu vào trong Hbox và xuất nó ra
            }

            // HBox câu trả lời
            cautraloi.getChildren().clear();

            HashSet<Character> kytucautraloi = new HashSet<>();// Tạo tập hợp set để lưu ký tự theo thứ tự ngẫu nhiên

            for (char character : word.toCharArray())
            {// Vòng lặp lấy từng ký tự của word lưu vào tập hợp set
                kytucautraloi.add(character);
            }

            Random random = new Random();
            for (int i = 0; i < 4; i++)
            {
                // Chọn ngẫu nhiên phần tử từ mảng a
                Character newItem = (char) (random.nextInt(26) + 'a'); // Thay đổi thành cách ngẫu nhiên khác nếu cần

                // Kiểm tra trùng lặp và random lại nếu cần
                while (kytucautraloi.contains(newItem))
                {
                    newItem = (char) (random.nextInt(26) + 'a'); // Thay đổi thành cách ngẫu nhiên khác nếu cần
                }

                // Thêm phần tử mới vào mảng b
                kytucautraloi.add(newItem);
            }

            for (Character character : kytucautraloi)
            {// Vòng lặp lấy từng ký tự của trong kytucautraloi bỏ vào từng button

                Button kytu = new Button(String.valueOf(character));// Chuyển đổi đổi từng ký tự thành button

                // Định dạng hiển thị cho đáp án
                kytu.setFont(Font.font("System", FontWeight.BOLD, 20));
                setRandomColor(kytu);
                kytu.setTextFill(Color.WHITE);
//                kytu.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(5), Insets.EMPTY)));
                kytu.setAlignment(Pos.CENTER);
                kytu.setPrefSize(50, 50);

                
                kytu.setOnMousePressed(e ->{
                    String buttonText = kytu.getText();// tạo biến buttonText và gán kytu vào nó

                    boolean correctGuess = false;// tạo biến correctGuess để check true false

                    for (int i = 0; i < kytudapan.length; i++)
                    {
                        if (buttonText.equals(String.valueOf(kytudapan[i])) && star[i] == kytudapan[i])
                        {
                            correctGuess = false;

                        } else if (buttonText.equals(String.valueOf(kytudapan[i])))
                        {
                            star[i] = buttonText.charAt(0);
                            correctGuess = true;
                        }
                    }

                    if (!correctGuess){
                        if (luoc_choi > 1){
                            alert_fail();
                        }
                        giamGiaTri();
                        truDiemBD();
                        tang_err();
                        if (err_tang > 2){
                            sai3Lan();
                            disableAllButtonsKytu(true);
                            AlertFail_ALL();
                        }
                    }

                    dapan.getChildren().clear();

                    for (int i = 0; i < star.length; i++)
                    {
                        Label kytusaukhitraloi = new Label(String.valueOf(star[i]));

                        // Định dạng hiển thị cho đáp án
                        kytusaukhitraloi.setFont(Font.font("System", FontWeight.BOLD, 40));
                        kytusaukhitraloi.setTextFill(Color.BLUE);
                        kytusaukhitraloi.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                        kytusaukhitraloi.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

                        // Kích thước tối thiểu của Label
                        kytusaukhitraloi.setPrefSize(60, 60);  // Dài, Rộng

                        // Canh vị trí chữ nằm giữa Label
                        kytusaukhitraloi.setAlignment(Pos.CENTER);

                        // Khoảng cách giữa các thành phần bên trong HBox
                        HBox.setMargin(kytusaukhitraloi, new Insets(10, 10, 10, 10));

                        dapan.getChildren().add(kytusaukhitraloi);
                    }//end for

                    // Add your style setting code here
                    kytu.setStyle("-fx-background-color: #0465ff; -fx-background-radius: 5;");
                    // ham style hover

                    if (word.equals(new String(star)))
                    {
                        disableAllButtonsKytu(true);
                        check_True = true;
                        AlertTrue();
                    }

                });

                kytu.setOnMouseReleased(e -> kytu.setStyle("-fx-background-color: blue; -fx-background-radius: 5;"));

                FlowPane.setMargin(kytu, new Insets(10, 10, 10, 10));

                cautraloi.getChildren().add(kytu);
            }
        }
    }

    private void Display_TongKet() {

        TKcol_STT.setCellValueFactory(new PropertyValueFactory<>("Stt"));

        TKcol_CAUHOI.setCellValueFactory(new PropertyValueFactory<>("Cauhoi"));

        TKcol_DAPAN.setCellValueFactory(new PropertyValueFactory<>("Dapan"));

        TKcol_DIEM.setCellValueFactory(new PropertyValueFactory<>("Diem"));

        tableTK.setItems(danhSachTongKet);

        TKcol_STT.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-alignment: CENTER;");
        TKcol_CAUHOI.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-alignment: CENTER;");
        TKcol_DAPAN.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-alignment: CENTER;");
        TKcol_DIEM.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-alignment: CENTER;");

        tableTK.setStyle("-fx-font-family: 'Arial';");

        if (!HT_tongDiem.getChildren().isEmpty())
        {
            HT_tongDiem.getChildren().clear();
        }

        int totalScore = Tinh_TongDiem();

        HT_tongDiem.getChildren().addAll(new Text(String.valueOf("Tổng điểm\n\n" + totalScore)));

    }

    private void Display_BXH() {

        BXHcol_DIEM.setCellValueFactory(new PropertyValueFactory<>("_tong_diem"));
        BXHcol_NGAY.setCellValueFactory(new PropertyValueFactory<>("_ngay_luu"));

        ObservableList<bangxepHang> itemBXH = FXCollections.observableArrayList();

        List<bangxepHang> luuBXH = function.select_BXH();

        itemBXH.addAll(luuBXH);

        tableBXH.setItems(itemBXH);

        BXHcol_DIEM.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-alignment: CENTER;");
        BXHcol_NGAY.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-alignment: CENTER;");

        tableBXH.setStyle("-fx-font-family: 'Arial';");

    }

    private void Display_BDIEM() {
        BDcol_DIEM.setCellValueFactory(new PropertyValueFactory<>("_tong_diem"));
        BDcol_NGAY.setCellValueFactory(new PropertyValueFactory<>("_ngay_luu"));
        BDcol_TIME.setCellValueFactory(new PropertyValueFactory<>("_time_luu"));

        ObservableList<bangxepHang> items = FXCollections.observableArrayList();

        List<bangxepHang> luuBangDiem = function.select_BDIEM();

        items.addAll(luuBangDiem);

        tableBDIEM.setItems(items);

        BDcol_DIEM.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-alignment: CENTER;");
        BDcol_NGAY.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-alignment: CENTER;");
        BDcol_TIME.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-alignment: CENTER;");

        tableBDIEM.setStyle("-fx-font-family: 'Arial';");
    }

    private void saveData() {
        TongKetResult newTongket = new TongKetResult(page, definition, word, diem_BD);
        danhSachTongKet.add(newTongket);
        Luu_diem_BD[page - 1] = diem_BD;
    }

    private int Tinh_TongDiem() {
        tongDiem = 0;
        for (int i = 0; i < maxIterations; i++)
        {
            tongDiem += Luu_diem_BD[i];
        }
        return tongDiem;
    }

    private void update_Tongdiem() {
        String ngay = function.DateDisplay();
        String thoi_gian = function.TimeDisplay();
        function.insert_TdN_database(tongDiem, ngay, thoi_gian);

    }

    private void resetGame() {
        saveData();
        DisplayNextWord();
        CountonPage();
        resetGiatri();
        reset_err();
        capNhapDiemBD();
    }

    private void CountonPage() {
        if (page < maxIterations)
        {
            page++;
            sotrang.setText(String.valueOf(page));
        } else
        {
            if (page == maxIterations)
            {
                HangmanCODEGAME.setVisible(false);
                HangmanXEPHANG.setVisible(false);
                HangmanTONGKET.setVisible(true);
                Display_TongKet();
                update_Tongdiem();
            }
        }
    }
    
//===========================các method xử lý lược đoán ký tự===================

    private int tang_err(){
        err_tang++;
        return err_tang;
    }

    private int reset_err(){
        err_tang = 0;
        return err_tang;
    } 
    
    private void giamGiaTri() {
        if (luoc_choi > 0){
            luoc_choi--;
            updateImageViews();
        }
    }

    private void resetGiatri() {
        luoc_choi = 3;
        updateImageViews();
    }

    private void updateImageViews() {
        luocChoi.getChildren().clear();
        for (int i = 0; i < luoc_choi; i++)
        {
            hinh_luoc_choi = new ImageView(new Image("/image/299063_heart_icon.png"));
            hinh_luoc_choi.setFitWidth(30);
            hinh_luoc_choi.setFitHeight(30);
            HBox.setMargin(hinh_luoc_choi, new Insets(8, 0, 0, 4));
            luocChoi.getChildren().add(hinh_luoc_choi);
        }
    }
//==================================các method xử lý điểm=======================

    private int truDiemBD() {
        if (diem_BD > 0)
        {
            diem_BD--;
        }
        return diem_BD;
    }

    private int sai3Lan() {
        diem_BD = 0;
        return diem_BD;
    }

    private void capNhapDiemBD() {
        diem_BD = 5;
    }

//================ method khi hoàn thành câu hỏi thì khóa các nút lại===========
    private void disableAllButtonsKytu(boolean disable) {// hàm vô hiệu hóa hết các nút kytu
        for (Node node : cautraloi.getChildren())
        {
            if (node instanceof Button button)
            {
                button.setDisable(disable);
            }
        }
    }

//==================================các method xuất thông báo===================
    private void Alert_BatDau() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("THÔNG BÁO");
        alert.setHeaderText("giới thiệu trò chơi:\n1. câu hỏi được hoàn thành khi bạn đã chọn hết các kí có trong ô\n2. nếu bạn nhấn nút next thì bạn sẻ bỏ qua câu hỏi.");
        ImageView icon = new ImageView("/image/hello.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.show();
    }

    private void AlertTrue() { // hàm in thông báo nếu thắng
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("THÔNG BÁO");
        alert.setHeaderText("bạn đã làm đúng\nbạn nhận được: " + diem_BD);
        ImageView icon = new ImageView("/image/winnroi.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.show();
    }

    private void AlertFail_ALL() { //hàm in thông báo nếu thua
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("THÔNG BÁO");
        alert.setHeaderText("bạn đã thua\nbạn nhận được: " + diem_BD);
        ImageView icon = new ImageView("/image/thuaroi.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.show();
    }

    private void alert_fail() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("THÔNG BÁO");
        alert.setHeaderText("ký tự không đúng");
        ImageView icon = new ImageView("/image/chonsairoi.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.show();
        // Tạo một PauseTransition với thời gian delay là 1 giây (1000 milliseconds)
        PauseTransition delay = new PauseTransition(Duration.millis(2000));
        delay.setOnFinished(event -> alert.close()); // Đóng thông báo khi kết thúc delay
        delay.play();
    }

//==================================các method đổi màu nút======================
    private void setRandomColor(Button button) {// Hàm để đặt màu ngẫu nhiên cho nút
        Color randomColor = getRandomColor();
        String hexColor = toRGBCode(randomColor);
        String style = String.format("-fx-base: %s;", hexColor);
        button.setStyle(style);
    }

    private Color getRandomColor() {    // Hàm để tạo một màu ngẫu nhiên
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private String toRGBCode(Color color) {     // Hàm để chuyển đổi màu RGB thành mã hex
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
