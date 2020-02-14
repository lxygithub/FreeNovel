package com.mewlxy.readlib.model;

import com.mewlxy.readlib.Constant;
import com.mewlxy.readlib.interfaces.OnChaptersListener;
import com.mewlxy.readlib.utlis.FileUtils;
import com.mewlxy.readlib.utlis.IOUtils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Created by newbiechen on 17-5-8.
 * 存储关于书籍内容的信息(CollBook(收藏书籍),BookChapter(书籍列表),ChapterInfo(书籍章节),BookRecord(记录),BookSignTable书签)
 */

public abstract class BookRepository {


    public abstract void saveBookRecord(ReadRecordBean mBookRecord);

    public abstract ReadRecordBean getBookRecord(String bookId);

    public abstract void chapterBeans(@NotNull BookBean mCollBook, @NotNull OnChaptersListener onChaptersListener);

    public abstract void requestChapterContents(@NotNull BookBean mCollBook, @NotNull List<ChapterBean> requestChapters, @NotNull OnChaptersListener onChaptersListener);

    public abstract void saveCollBook(BookBean mCollBook);

    public abstract void saveBookChaptersWithAsync(List<ChapterBean> bookChapterBeanList, BookBean mCollBook);


    /**
     * 存储章节
     *
     * @param folderName
     * @param fileName
     * @param content
     */
    public void saveChapterInfo(String folderName, String fileName, String content) {
        String filePath = Constant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_NB;
        if (new File(filePath).exists()) {
            return;
        }
        String str = content.replaceAll("\\\\n\\\\n", "\n");
        File file = BookManager.getBookFile(folderName, fileName);
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(str);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.INSTANCE.close(writer);
        }
    }
}
