package com.mewlxy.readlib.page;



import com.mewlxy.readlib.Constant;
import com.mewlxy.readlib.model.ChapterBean;
import com.mewlxy.readlib.model.BookManager;
import com.mewlxy.readlib.model.BookRepository;
import com.mewlxy.readlib.model.BookBean;
import com.mewlxy.readlib.utlis.FileUtils;
import com.mewlxy.readlib.utlis.MD5Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zlj
 * 网络页面加载器
 */
public class NetPageLoader extends PageLoader {

    private BookRepository bookRepository;

    public NetPageLoader(PageView pageView, BookBean collBook, BookRepository bookRepository) {
        super(pageView, collBook,bookRepository);
        this.bookRepository = bookRepository;
    }

    @Override
    public void refreshChapterList() {
        if (mCollBook.getChapters().isEmpty()) return;

        mChapterList = mCollBook.getChapters();
        isChapterListPrepare = true;

        // 目录加载完成，执行回调操作。
        if (mPageChangeListener != null) {
            mPageChangeListener.onChaptersFinished(mChapterList);
        }

        // 如果章节未打开
        if (!isChapterOpen()) {
            // 打开章节
            openChapter();
        }
    }

    @Override
    public void openSpecifyChapter(int specifyChapter) {
//        mCurChapterPos = specifyChapter;
//        refreshChapterList();
    }

    @Override
    protected BufferedReader getChapterReader(ChapterBean chapter) throws Exception {
        File file = new File(Constant.BOOK_CACHE_PATH
                + MD5Utils.INSTANCE.strToMd5By16(mCollBook.getUrl())
                + File.separator + chapter.getName() + FileUtils.SUFFIX_NB);
        if (!file.exists()) return null;
        Reader reader = new FileReader(file);
        return new BufferedReader(reader);
    }

    @Override
    protected boolean hasChapterData(ChapterBean chapter) {
        return BookManager.isChapterCached(MD5Utils.INSTANCE.strToMd5By16(mCollBook.getUrl()), chapter.getName());
    }

    // 装载上一章节的内容
    @Override
    boolean parsePrevChapter() {
        boolean isRight = super.parsePrevChapter();

        if (mStatus == STATUS_FINISH) {
            loadPrevChapter();
        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
        }
        return isRight;
    }

    // 装载当前章内容。
    @Override
    boolean parseCurChapter() {
        boolean isRight = super.parseCurChapter();

        if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
        }
        return isRight;
    }

    // 装载下一章节的内容
    @Override
    boolean parseNextChapter() {
        boolean isRight = super.parseNextChapter();

        if (mStatus == STATUS_FINISH) {
            loadNextChapter();
        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
        }

        return isRight;
    }

    /**
     * 加载当前页的前面两个章节
     */
    private void loadPrevChapter() {
        if (mPageChangeListener != null) {
            int end = mCurChapterPos;
            int begin = end - 2;
            if (begin < 0) {
                begin = 0;
            }

            requestChapters(begin, end);
        }
    }

    /**
     * 加载前一页，当前页，后一页。
     */
    private void loadCurrentChapter() {
        if (mPageChangeListener != null) {
            int begin = mCurChapterPos;
            int end = mCurChapterPos;

            // 是否当前不是最后一章
            if (end < mChapterList.size()) {
                end = end + 1;
                if (end >= mChapterList.size()) {
                    end = mChapterList.size() - 1;
                }
            }

            // 如果当前不是第一章
            if (begin != 0) {
                begin = begin - 1;
                if (begin < 0) {
                    begin = 0;
                }
            }

            requestChapters(begin, end);
        }
    }

    /**
     * 加载当前页的后两个章节
     */
    private void loadNextChapter() {
        if (mPageChangeListener != null) {

            // 提示加载后两章
            int begin = mCurChapterPos + 1;
            int end = begin + 1;

            // 判断是否大于最后一章
            if (begin >= mChapterList.size()) {
                // 如果下一章超出目录了，就没有必要加载了
                return;
            }

            if (end > mChapterList.size()) {
                end = mChapterList.size() - 1;
            }

            requestChapters(begin, end);
        }
    }

    private void requestChapters(int start, int end) {
        // 检验输入值
        if (start < 0) {
            start = 0;
        }

        if (end >= mChapterList.size()) {
            end = mChapterList.size() - 1;
        }


        List<ChapterBean> chapters = new ArrayList<>();

        // 过滤，哪些数据已经加载了
        for (int i = start; i <= end; ++i) {
            ChapterBean txtChapter = mChapterList.get(i);
            if (!hasChapterData(txtChapter)) {
                chapters.add(txtChapter);
            }
        }

        if (!chapters.isEmpty()) {
            mPageChangeListener.chapterContents(chapters);
        }
    }

    @Override
    public void saveRecord() {
        super.saveRecord();
        if (mCollBook != null && isChapterListPrepare) {
            //表示当前CollBook已经阅读
            mCollBook.setUpdate(0);
            mCollBook.setLastRead(String.valueOf(System.currentTimeMillis()));
            //直接更新
            bookRepository.saveCollBook(mCollBook);
        }
    }
}

