package vn.edu.iuh.fit.backend.converters;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConvertTime {
    public static String calculateTimeAgo(LocalDateTime jobCreateAt) {
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Tính khoảng cách giữa thời gian hiện tại và thời gian của công việc
        Duration duration = Duration.between(jobCreateAt, now);

        // Kiểm tra các khoảng thời gian khác nhau
        if (duration.toDays() > 0) {
            long days = duration.toDays();
            return days + " ngày trước";
        } else if (duration.toHours() > 0) {
            long hours = duration.toHours();
            return hours + " giờ trước";
        } else if (duration.toMinutes() > 0) {
            long minutes = duration.toMinutes();
            return minutes + " phút trước";
        } else {
            return "Vừa xong";
        }
    }

    public static String calculateExpire(LocalDateTime jobCreateAt) {
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Tính khoảng cách giữa thời gian hiện tại và thời gian của công việc
        Duration duration = Duration.between(jobCreateAt, now);

        // Kiểm tra các khoảng thời gian khác nhau
        if (duration.toDays() <= 0) {
            long days = -duration.toDays();
            return "Còn "+days + " ngày để ứng tuyển";
        } else if (duration.toHours() <= 0) {
            long hours = -duration.toHours();
            return "Còn "+ hours + " giờ để ứng tuyển";
        } else if (duration.toMinutes() <= 0) {
            long minutes = -duration.toMinutes();
            return "Còn "+minutes + " phút để ứng tuyển";
        } else {
            return "Hết hạn";
        }
    }
}
