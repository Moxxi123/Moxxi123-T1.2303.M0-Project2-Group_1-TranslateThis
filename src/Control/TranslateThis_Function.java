package Control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TranslateThis_Function {

    // KHUNG MENU
    // Kiểm tra kết nối đến Google Script
    public boolean CheckConnection() {

        String strUrl = "https://script.google.com/macros/s/AKfycbxWHgZs8UhEf0ZYbr6oCoqfD5lhpowhoDvUgXaHIfdOZG2EImS7NYuv0xlXAX6rbDFh/exec";

        try
        {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();

            int responseCode = urlConn.getResponseCode();

            switch (responseCode)
            {
                case HttpURLConnection.HTTP_OK ->
                {
                    return true;
                }
                case 429 ->
                {
                    System.err.println("Quota limit reached. Too many requests.");
                    return false;
                }
                default ->
                {
                    System.err.println("Error: " + responseCode);
                    return false;
                }
            }
        } catch (IOException e)
        {
            System.err.println("Error creating HTTP connection");
            //e.printStackTrace();
            return false;
        }
    }

    // Hiển thị ngày
    public String DateDisplay() {
        Date date = new Date();
        SimpleDateFormat simpledate = new SimpleDateFormat("dd/MM/yyyy");
        String displaydate = simpledate.format(date);
        return displaydate;
    }

    // Kết nối đến database SQLite
    public Connection getConnect() {

        Connection cn = null;

        try
        {
            //String url = "jdbc:sqlite:Data\\TranslateThis.db"; //Không chạy trên file jar nhưng chạy trên netbean
            String url = "jdbc:sqlite:..\\Data\\TranslateThis.db"; //Không chạy trên netbean nhưng chạy trên file jar

            cn = DriverManager.getConnection(url);
            //System.out.println("Connect successful to SQLite database.");
        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }
        return cn;
    }

    // TRANG TỪ ĐIỂN
    // Tìm kiếm trong database từ gợi ý
    public List<String> DisplaySuggestions(String text) {
        List<String> suggestions = new ArrayList<>();

        String statement = "SELECT word FROM AnhViet WHERE word LIKE ?";

        try (Connection cn = getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            preparedStatement.setString(1, text + "%");

            ResultSet tableresult = preparedStatement.executeQuery();

            while (tableresult.next())
            {
                suggestions.add(tableresult.getString("word"));
            }

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }

        return suggestions;
    }

    // Dịch từ qua Google Script
    public static String OnlineTranslate(String text) throws IOException {

        String langFrom = "en"; // English
        String langTo = "vi";   // Vietnamese

        String urlStr = "https://script.google.com/macros/s/AKfycbxWHgZs8UhEf0ZYbr6oCoqfD5lhpowhoDvUgXaHIfdOZG2EImS7NYuv0xlXAX6rbDFh/exec"
                + "?q=" + URLEncoder.encode(text, "UTF-8")
                + "&target=" + langTo
                + "&source=" + langFrom;
        URL url = new URL(urlStr);
        // tạo stringbuilder để lưu kết quả trả về từ Google Script
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        // tạo stream để đọc kết quả trả về từ Google Script 
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        // đọc từng dòng trong stream để lưu vào chuỗi inputLine
        while ((inputLine = in.readLine()) != null)
        {
            // lưu từng dòng inputLine vào trong string builder response
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    // Dịch từ qua từ điển
    public List<Word> OfflineTranslate(String table, String text) {

        List<Word> result = new ArrayList<>();

        String statement = "SELECT id, word, pronounce, type, definition, [update], updateddefinition FROM " + table + " WHERE word = ?";

        try (Connection cn = getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            preparedStatement.setString(1, text);

            ResultSet tableresult = preparedStatement.executeQuery();

            while (tableresult.next())
            {
                Integer id = tableresult.getInt(1);
                String word = tableresult.getString(2);
                String pronounce = tableresult.getString(3);
                String type = tableresult.getString(4);
                String definition = tableresult.getString(5);
                String update = tableresult.getString(6);
                String updateddefinition = tableresult.getString(7);

                Word information = new Word(id, word, pronounce, type, definition, update, updateddefinition);

                result.add(information);
            }

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }

        return result;
    }

    // Thêm từ
    public boolean AddWord(String table, String text, String result) {
        String statement = "INSERT INTO " + table + " (word, pronounce, type, definition) VALUES (?, 'null', 'null', ?)";

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            //điền giá trị cho các cột tham số ?
            dostatement.setString(1, text);
            dostatement.setString(2, result);

            //thực hiện truy vấn
            int rowinsert = dostatement.executeUpdate();

            return rowinsert > 0;

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
            return false;
        }
    }

    // Kiểm tra định nghĩa của từ có trong danh sách từ điển
    public String CheckWordDefinitionInData(String table, String text) {

        String result = null;

        String statement = "SELECT definition FROM " + table + " WHERE word = ?";

        try (Connection cn = getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            //điền giá trị cho các cột tham số ?
            preparedStatement.setString(1, text);

            //thực hiện truy vấn
            ResultSet tableresult = preparedStatement.executeQuery();

            if (tableresult.next())
            {
                result = tableresult.getString("definition");
            }

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }

        return result;
    }

    public String CheckWordUpdatedDefinitionInData(String table, String text) {

        String result = null;

        String statement = "SELECT updateddefinition FROM " + table + " WHERE word = ?";

        try (Connection cn = getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            //điền giá trị cho các cột tham số ?
            preparedStatement.setString(1, text);

            //thực hiện truy vấn
            ResultSet tableresult = preparedStatement.executeQuery();

            if (tableresult.next())
            {
                result = tableresult.getString("updateddefinition");
            }

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }

        return result;
    }

    // Cập nhập định nghĩa từ từ Google Script
    public boolean UpdateDefinition(String table, String text, String update, String updateddefinition) {

        String statement = "UPDATE " + table + " SET [update] = ?, updateddefinition = ? WHERE word = ?";

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            //điền giá trị cho các cột tham số ?
            dostatement.setString(1, update);
            dostatement.setString(2, updateddefinition);
            dostatement.setString(3, text);

            //thực hiện truy vấn
            int rowupdate = dostatement.executeUpdate();

            return rowupdate > 0;
        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
            return false;
        }
    }

    // NHUNG
    // TRANG DANH SÁCH TỪ
    // lấy dữ liệu vào tableview 
    public ObservableList<ListView> GetWordForTable(String tablechoice, String searchword, String typechoice) {

        ObservableList<ListView> tablelist = FXCollections.observableArrayList(); // tạo danh sách ObservableList để chưa đối tượng ListView 

        String statement = "SELECT word, pronounce, type, definition, [check] FROM " + tablechoice + " WHERE type LIKE ? AND word LIKE ?"; //câu lệnh truy vấn SQL lấy dự liệu từ bảng 

        try (Connection cn = getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            //điền giá trị cho các cột tham số ?

            preparedStatement.setString(2, searchword); // từ khóa tìm kiếm trong cột "word" 
            preparedStatement.setString(1, typechoice); // từ khóa tìm kiếm trong côt "type"

            //thực hiện truy vấn và kết quả lưu vào tableresult
            ResultSet tableresult = preparedStatement.executeQuery();

            while (tableresult.next())
            {
                // trong mỗi dòng đọc từng cột dữ liệu của tableresult và lưu vào các biến tương ứng trong lớp ListView
                int check = tableresult.getInt(5);
                String word = tableresult.getString(1);
                String pronounce = tableresult.getString(2);
                String type = tableresult.getString(3);
                String definition = tableresult.getString(4);

                // gọi hàm dựng của ListView để thêm data vào
                ListView wordlist = new ListView(check, word, pronounce, type, definition, tablechoice);

                //lưu đối tượng 'wordlist' vào mảng ObservableList tablelist
                tablelist.add(wordlist);

            }

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }

        return tablelist;
    }

    // cập nhập từng select box
    // Hàm 'UpdateselectBox' dùng cập nhật giá trị cột 'check' 
    public void UpdateSelectBox(String tablechoice, int value, String text) {

        String statement = "UPDATE " + tablechoice + " SET [check] = ? WHERE word = ?"; // lệnh SQL cập nhật giá trị cột 'check' cho cột 'word'

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            //điền giá trị 'value' 'text 'cho các cột tham số 1, 2
            dostatement.setInt(1, value);
            dostatement.setString(2, text);

            //thực hiện truy vấn Update
            dostatement.executeUpdate();

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    // cập nhập nhiều select box
    public void UpdateAllSelectBox(String tablechoice, int value, String searchword, String typechoice) {

        String statement = "UPDATE " + tablechoice + " SET [check] = ? WHERE type LIKE ? AND word LIKE ?";

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            //điền giá trị cho các cột tham số ?
            dostatement.setInt(1, value);
            dostatement.setString(2, typechoice);
            dostatement.setString(3, searchword);

            //thực hiện truy vấn
            dostatement.executeUpdate();

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    // reset lại check
    public void ResetSelectBox(String tablechoice) {

        String statement = "UPDATE " + tablechoice + " SET [check] = 0";

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            //thực hiện truy vấn
            dostatement.executeUpdate();

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    //hàm Count đếm số lượng cột check đang được chọn
    public int CountSelectBox(String tablechoice) {
        int count = 0;

        String statement = "SELECT COUNT(*) FROM " + tablechoice + " WHERE [check] = 1;";

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            //thực hiện truy vấn
            ResultSet tableresult = dostatement.executeQuery();

            while (tableresult.next())
            {
                count = tableresult.getInt(1);
            }

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }

        return count;
    }

    // kiểm tra từ có trong danh sách từ điển
    public String CheckWordInData(String table, String text) {

        String result = null;

        String statement = "SELECT word FROM " + table + " WHERE word = ?";

        try (Connection cn = getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            //điền giá trị cho các cột tham số ?
            preparedStatement.setString(1, text);

            //thực hiện truy vấn
            ResultSet tableresult = preparedStatement.executeQuery();

            if (tableresult.next())
            {
                result = tableresult.getString("word");
            }

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }

        return result;
    }

    // lấy data cho trang Edit
    public List<Word> GetWordForUpdate(String table) {

        List<Word> result = new ArrayList<>();

        String statement = "SELECT id, word, pronounce, type, definition, [update], updateddefinition FROM " + table + " WHERE [check] = 1";

        try (Connection cn = getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            //thực hiện truy vấn
            ResultSet tableresult = preparedStatement.executeQuery();

            while (tableresult.next())
            {
                // trong mỗi dòng đọc từng cột dữ liệu của tableresult và lưu vào các biến tương ứng trong lớp Word
                Integer id = tableresult.getInt(1);
                String word = tableresult.getString(2);
                String pronounce = tableresult.getString(3);
                String type = tableresult.getString(4);
                String definition = tableresult.getString(5);
                String update = tableresult.getString(6);
                String updateddefinition = tableresult.getString(7);

                // gọi hàm dựng của Word để thêm data vào
                Word information = new Word(id, word, pronounce, type, definition, update, updateddefinition);

                //lưu đối tượng vào mảng result
                result.add(information);
            }

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }

        return result;
    }

    // cập nhập thông tin từ vựng
    public boolean UpdateWordInformation(String tablechoice, String updateword, String updatepronounce, String updatetype, String updatedefinition, String updatedate, String updatenewdefinition, int updateid) {

        String statement = "UPDATE " + tablechoice + " SET word = ?, pronounce = ?, type = ?, definition = ?, [update] = ?,  updateddefinition = ? WHERE id = ?";

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            //điền giá trị cho các cột tham số ?
            dostatement.setString(1, updateword);
            dostatement.setString(2, updatepronounce);
            dostatement.setString(3, updatetype);
            dostatement.setString(4, updatedefinition);
            dostatement.setString(5, updatedate);
            dostatement.setString(6, updatenewdefinition);
            dostatement.setInt(7, updateid);

            //thực hiện truy vấn
            int rowupdate = dostatement.executeUpdate();

            return rowupdate > 0;

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
            return false;
        }
    }

    // Hàm thêm từ vựng
    public boolean AddWordInformation(String tablechoice, String addword, String addpronounce, String addtype, String adddefinition) {

        String statement = "INSERT INTO " + tablechoice + " (word, pronounce, type, definition, [check]) VALUES (?, ?, ?, ?, 0)"; //câu lệnh SQL

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            //điền giá trị cho các cột tham số ?
            dostatement.setString(1, addword);
            dostatement.setString(2, addpronounce);
            dostatement.setString(3, addtype);
            dostatement.setString(4, adddefinition);

            //thực hiện truy vấn
            int rowinsert = dostatement.executeUpdate();

            return rowinsert > 0;

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
            return false;
        }
    }

    // Hàm xóa từ vựng theo cột 'check'
    public boolean DeleteWordInformation(String tablechoice) {

        String statement = "DELETE FROM " + tablechoice + " WHERE [check] = 1";

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            //thực hiện truy vấn
            int rowdelete = dostatement.executeUpdate(); //biến 'rowdelete'  kiểu int dùng để lưu trữ hàng bị xóa

            return rowdelete > 0; //nếu giá trị lớn hơn 0 thì trả về true, ngược lại là false.

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
            return false;
        }
    }

    // Đếm số lượng từ vựng sẽ được thêm từ hệ thống
    public int CountAddSysTemData(String tablechoice) {
        int count = 0;

        String statement = "SELECT COUNT(*) FROM " + tablechoice + "_DefaultData WHERE word NOT IN (SELECT word FROM " + tablechoice + ")";

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            // Thực hiện truy vấn
            ResultSet tableresult = dostatement.executeQuery();

            while (tableresult.next())
            {
                count = tableresult.getInt(1); // Lấy cột duy nhất của bảng tableresult là cột count
            }

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
        }

        return count;
    }

    // Thêm từ vựng từ hệ thống
    public boolean AddSystemData(String tablechoice) {
        String statement = "INSERT INTO " + tablechoice + " (word, pronounce, type, definition) SELECT word, pronounce, type, definition FROM " + tablechoice + "_DefaultData WHERE word NOT IN (SELECT word FROM " + tablechoice + ")";

        try (Connection cn = getConnect(); PreparedStatement dostatement = cn.prepareStatement(statement))
        {
            // Thực hiện truy vấn
            int rowinsert = dostatement.executeUpdate();

            return rowinsert > 0;

        } catch (SQLException ex)
        {
            System.err.println("Error: " + ex.getMessage());
            return false;
        }
    }

    // HUY
    // TRANG HANGMAN GAME
// Hiển thị time
    public String TimeDisplay() {
        Date time = new Date();
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
        String displayTime = simpleTimeFormat.format(time);
        return displayTime;
    }

// Phương thức để kết nối đến cơ sở dữ liệu và lấy số tăng tự động đầu tiên
    public int firstIDENTITYDatabase() throws SQLException {
        int scopeIdentity = 0;
        String statement = "SELECT id FROM AnhViet ORDER BY id ASC LIMIT 1;";
        TranslateThis_Function b = new TranslateThis_Function();

        try (Connection cn = b.getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    scopeIdentity = resultSet.getInt("id");
                }
            }
        }

        return scopeIdentity;
    }

// Phương thức để kết nối đến cơ sở dữ liệu và lấy số tăng tự động cuối cùng
    public int lastIDENTITYDatabase() throws SQLException {
        int scopeIdentity = 0;
        String statement = "SELECT id FROM AnhViet ORDER BY id DESC LIMIT 1;";
        TranslateThis_Function b = new TranslateThis_Function();
        try (Connection cn = b.getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    scopeIdentity = resultSet.getInt("id");
                }
            }
        }
        return scopeIdentity;
    }

// Lấy 20 số ngẫu nhiên và trích xuất từ database word và definition tương ứng với 20 số ngẫu nhiên đó
    public LinkedHashMap<String, String> Select_WD_Database() throws SQLException {
    LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
    String statement = "SELECT word, definition FROM AnhViet WHERE ID = ?";
    TranslateThis_Function b = new TranslateThis_Function();

    try (Connection cn = b.getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement)) {
        Random rd = new Random();
        int firstIdentity = b.firstIDENTITYDatabase();
        int lastIdentity = b.lastIDENTITYDatabase();

        if (firstIdentity == 0 && lastIdentity == 0) { // Trường hợp không có dữ liệu trong database => id = 0 => first, last = 0 đặt luôn dataMap là empty
            return dataMap;
        }

        int attempts = 0; // số lần thử (chặn trường hợp vòng lặp chạy vô tận từ VD đang có 19 từ mà while phải chạy = 20 từ thì mới thoát vòng lặp => trường hợp 19 từ thì vòng lặp chạy vô tận
        while (dataMap.size() < 20) { // Vòng lặp lấy đủ 20 từ vựng 
            if (attempts > 30) { // Kiếm tra số lần thử, nếu lớn hơn 30 thì dừng vòng lặp (Lưu ý: số lần thử phải lớn hơn 20 vì nếu nhỏ hơn vòng lặp sẽ bị phá trước khi đủ 20 từ)
                break;
            }
            
            int randomNumber = rd.nextInt(lastIdentity - firstIdentity + 1) + firstIdentity;
            preparedStatement.setInt(1, randomNumber);

            try (ResultSet tableresult = preparedStatement.executeQuery()) {
                if (tableresult.next()) {
                    String word = tableresult.getString("word");
                    String definition = tableresult.getString("definition");
                    if (!dataMap.containsKey(word)) {
                        dataMap.put(word, definition);
                    }
                }
            }
            attempts++; // Chạy xong 1 vòng thì tăng số lần thử lên + 1
        }
        
        if(attempts > 30){
            dataMap.clear(); // Nếu đã vượt quá 30 lần thử mà chưa đủ 20 từ thì clear dataMap => dataMap empty
        }
    }
    return dataMap;
}

// Sử dụng từ vựng của hệ thống trong trường hợp Danh sách từ không đủ từ vựng để bắt đầu trò chơi
    public int firstIDENTITYDatabaseDefault() throws SQLException {
        int scopeIdentity = 0;
        String statement = "SELECT id FROM AnhViet_DefaultData ORDER BY id ASC LIMIT 1;";
        TranslateThis_Function b = new TranslateThis_Function();

        try (Connection cn = b.getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    scopeIdentity = resultSet.getInt("id");
                }
            }
        }

        return scopeIdentity;
    }

    public int lastIDENTITYDatabaseDefault() throws SQLException {
        int scopeIdentity = 0;
        String statement = "SELECT id FROM AnhViet_DefaultData ORDER BY id DESC LIMIT 1;";
        TranslateThis_Function b = new TranslateThis_Function();
        try (Connection cn = b.getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    scopeIdentity = resultSet.getInt("id");
                }
            }
        }
        return scopeIdentity;
    }

    public LinkedHashMap<String, String> Select_WD_DatabaseBackKup() throws SQLException {
        LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
        String statement = "SELECT word, definition FROM AnhViet_DefaultData WHERE ID = ?";
        TranslateThis_Function b = new TranslateThis_Function();

        try (Connection cn = b.getConnect(); PreparedStatement preparedStatement = cn.prepareStatement(statement))
        {
            Random rd = new Random();
            int firstIdentity = b.firstIDENTITYDatabaseDefault();
            int lastIdentity = b.lastIDENTITYDatabaseDefault();

            while (dataMap.size() < 20)
            { // vòng lặp while đảm bảo phải đủ 20 số trong dataMap
                int randomNumber = rd.nextInt(lastIdentity - firstIdentity + 1) + firstIdentity;
                preparedStatement.setInt(1, randomNumber);

                try (ResultSet tableresult = preparedStatement.executeQuery())
                {
                    if (tableresult.next())
                    { // kiểm tra xem số ngẫu nhiên có lấy giá trị tương ứng hay không (loại trừ trường hợp số ngẫu nhiên trùng với id bị xóa)
                        String word = tableresult.getString("word");
                        String definition = tableresult.getString("definition");

                        if (!dataMap.containsKey(word))
                        { // kiểm tra xem từ được thêm vào trong dataMap phải là duy nhất
                            dataMap.put(word, definition);
                        }
                    }
                }
            }
        }
        return dataMap;
    }

//    public static void main(String[] args) throws SQLException {
//        TranslateThis_Function p = new TranslateThis_Function();
//        LinkedHashMap<String, String> check = new LinkedHashMap<>();
//        check = p.Select_WD_Database();
//        if (check.isEmpty())
//        {
//            System.out.println("Check: No data found in the database.");
//        } else
//        {
//            for (Map.Entry<String, String> entry : check.entrySet())
//            {
//                System.out.println(entry.getKey() + ": " + entry.getValue());
//                System.out.println(); // Xuống dòng
//            }
//        }
//    }

    public List<bangxepHang> select_BXH() {
        ArrayList<bangxepHang> luuBang = new ArrayList<>();
        Connection connection = getConnect();

        try
        {
            String selectQuery = "WITH RankedRows AS ("
                    + "  SELECT *,"
                    + "         ROW_NUMBER() OVER (PARTITION BY tongdiem, ngay ORDER BY stt DESC) AS RowNum"
                    + "  FROM RanksData"
                    + ")"
                    + "SELECT * FROM RankedRows WHERE RowNum = 1"
                    + " ORDER BY tongdiem DESC, ngay ASC"
                    + " LIMIT 10";

            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                int stt = resultSet.getInt("stt");
                int tongdiem = resultSet.getInt("tongdiem");
                String ngay = resultSet.getString("ngay");
                String time = resultSet.getString("thoigian");
                bangxepHang newDS = new bangxepHang(stt, tongdiem, ngay, time);
                luuBang.add(newDS);
            }
        } catch (SQLException ex)
        {
            System.err.println("Lỗi trong quá trình thực hiện truy vấn SELECT: " + ex.getMessage());
        }
        return luuBang;
    }

    public List<bangxepHang> select_BDIEM() {
        ArrayList<bangxepHang> luuBang = new ArrayList<>();
        Connection connection = getConnect();

        try
        {
            String selectQuery = "SELECT * FROM RanksData ORDER BY ngay DESC, thoigian DESC;";

            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                int stt = resultSet.getInt("stt");
                int tongdiem = resultSet.getInt("tongdiem");
                String ngay = resultSet.getString("ngay");
                String time = resultSet.getString("thoigian");
                bangxepHang newDS = new bangxepHang(stt, tongdiem, ngay, time);
                luuBang.add(newDS);
            }
        } catch (SQLException ex)
        {
            System.err.println("Lỗi trong quá trình thực hiện truy vấn SELECT: " + ex.getMessage());
        }
        return luuBang;
    }

    // Method thực hiện truy vấn INSERT tongdiem và ngay
    public void insert_TdN_database(int add_tongDiem, String add_ngay, String add_time) {
        Connection connection = getConnect();

        try
        {
            String insertQuery = "INSERT INTO RanksData(tongdiem, ngay, thoigian) VALUES (? , ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            preparedStatement.setInt(1, add_tongDiem);

            preparedStatement.setString(2, add_ngay);

            preparedStatement.setString(3, add_time);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0)
            {
//                System.out.println("Dữ liệu đã được chèn thành công.");
            } else
            {
                System.out.println("Chèn dữ liệu không thành công.");
            }

        } catch (SQLException ex)
        {
            System.err.println("Lỗi trong quá trình thực hiện truy vấn INSERT: " + ex.getMessage());
        }
    }

}
