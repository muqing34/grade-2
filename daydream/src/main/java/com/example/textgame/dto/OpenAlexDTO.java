package com.example.textgame.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for OpenAlex API response (/works endpoint)
 * 只包含我们需要展示的部分字段
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略不需要的字段
public class OpenAlexDTO {

    private Meta meta;
    private List<Work> results;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private int count;
        private int page;
        @JsonProperty("per_page") // JSON 字段名与 Java 变量名不一致
        private int perPage;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Work {
        private String id;
        @JsonProperty("display_name")
        private String displayName; // 文章标题
        private String doi; // DOI 链接
        @JsonProperty("publication_year")
        private Integer publicationYear;
        private List<Authorship> authorships;
        private String citedByApiUrl; // 可以用来获取引用文献

        // 获取作者姓名的辅助方法
        public String getAuthorNames() {
            if (authorships == null || authorships.isEmpty()) {
                return "N/A";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < authorships.size() && i < 3; i++) { // 最多显示3个作者
                if (i > 0) sb.append(", ");
                sb.append(authorships.get(i).getAuthor().getDisplayName());
            }
            if (authorships.size() > 3) {
                sb.append(" et al.");
            }
            return sb.toString();
        }
        // 获取 DOI URL 的辅助方法
        public String getDoiUrl() {
            return doi != null ? doi.replace("https://doi.org/", "") : null;
        }
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Authorship {
        private Author author;
        // 可以添加 institution 等信息
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        private String id;
        @JsonProperty("display_name")
        private String displayName;
        private String orcid;
    }

}
