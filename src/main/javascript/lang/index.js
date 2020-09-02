import englishMessages from 'ra-language-english';

const chineseMessages = {
    ra : {
        action       : {
            add_filter        : '增加過濾器',
            add               : '新增',
            back              : '返回',
            bulk_actions      : '選擇了一個項目 |||| 選擇了 %{smart_count} 個項目',
            cancel            : '取消',
            clear_input_value : '清除',
            clone             : '複製',
            confirm           : '確認',
            create            : '新增',
            delete            : '刪除',
            edit              : '編輯',
            export            : '輸出',
            list              : '列表',
            refresh           : '更新',
            remove_filter     : '刪除過濾器',
            remove            : '清除',
            save              : '儲存',
            search            : '搜尋',
            show              : '顯示',
            sort              : '排序',
            undo              : '復原',
            unselect          : '取消選擇',
            expand            : '擴展',
            close             : '關閉',
            open_menu         : '打開選單',
            close_menu        : '關閉選單',
        },
        boolean      : {
            true  : '是',
            false : '否',
            null  : '',
        },
        page         : {
            create    : '新增 %{name}',
            dashboard : '儀表板',
            edit      : '%{name} #%{id}',
            error     : '出了些問題',
            list      : '%{name}',
            loading   : '載入中',
            not_found : '找不到',
            show      : '%{name} #%{id}',
            empty     : '暫無 %{name}',
            invite    : '你想新增一個嗎？',
        },
        input        : {
            file       : {
                upload_several : '拖放一些文件進行上傳，或單擊以選擇其中一個',
                upload_single  : '拖放文件以上傳，或單擊以選擇',
            },
            image      : {
                upload_several : '拖放一些圖片進行上傳，或單擊以選擇其中一張',
                upload_single  : '拖放圖片以上傳，或單擊以選擇',
            },
            references : {
                all_missing    : '找不到參考資料',
                many_missing   : '至少有一個關聯的項目不再可用',
                single_missing : '關聯的項目似乎不再可用',
            },
            password   : {
                toggle_visible : '隱藏密碼',
                toggle_hidden  : '顯示密碼',
            },
        },
        message      : {
            about               : '關於',
            are_you_sure        : '你確定嗎？',
            bulk_delete_content : '你確定要刪除這個 %{name} 嗎？ |||| 你確定要刪除這 %{smart_count} 個項目嗎？',
            bulk_delete_title   : '刪除 %{name} |||| 刪除 %{smart_count} 個 %{name}',
            delete_content      : '你確定要刪除這個項目嗎？',
            delete_title        : '刪除 %{name} #%{id}',
            details             : '詳細資料',
            error               : '發生用戶端錯誤，無法完成你的指令',
            invalid_form        : '該表格無效，請檢查錯誤',
            loading             : '該頁面正在載入中，請稍後',
            no                  : '否',
            not_found           : '你輸入了錯誤的鏈接，或者你訪問了錯誤的鏈接。',
            yes                 : '是',
            unsaved_changes     : '你的某些更改未儲存。你確定要忽略它們嗎？',
        },
        navigation   : {
            no_results             : '沒有找到結果',
            no_more_results        : '頁碼 %{page} 超越界限。請嘗試前一頁。',
            page_out_of_boundaries : '頁碼 %{page} 超越界限',
            page_out_from_end      : '無法去最後一頁之後',
            page_out_from_begin    : '無法去第一頁之前',
            page_range_info        : '%{total} 之 %{offsetBegin}-%{offsetEnd}',
            page_rows_per_page     : '每頁項目：',
            next                   : '下一頁',
            prev                   : '上一頁',
        },
        sort         : {
            sort_by : '排序方式 %{field} %{order}',
            ASC     : '升序',
            DESC    : '降序',
        },
        auth         : {
            auth_check_error : '請登入後繼續',
            user_menu        : '個人資料',
            username         : '用戶名',
            password         : '密碼',
            sign_in          : '登入',
            sign_in_error    : '驗證失敗，請重試',
            logout           : '登出',
        },
        notification : {
            updated             : '已更新項目 |||| %{smart_count} 個項目已更新',
            created             : '已新增項目',
            deleted             : '已刪除項目 |||| %{smart_count} 個項目已刪除',
            bad_item            : '不正確項目',
            item_doesnt_exist   : '項目不存在',
            http_error          : '服務器通訊錯誤',
            data_provider_error : 'dataProvider 錯誤。詳情請查看控制台。',
            i18n_error          : '無法載入指定語言的翻譯',
            canceled            : '操作已取消',
            logged_out          : '您的會期已結束，請重新連接',
        },
        validation   : {
            required  : '必要',
            minLength : '必須至少為 %{min} 個字',
            maxLength : '必須是 %{max} 個字或更小',
            minValue  : '必須至少為 %{min}',
            maxValue  : '必須是 %{max} 或更小',
            number    : '必須是數字',
            email     : '必須是有效的電子郵件',
            oneOf     : '必須是以下之一： %{options}',
            regex     : '必須符合特定格式 (regexp): %{pattern}',
        },
    },
};

export const lang = {
    chinese : {
        ...chineseMessages,
        labels    : {
            read_more : '更多',
            publisher : '新聞出版商',
        },
        resources : {
            港聞 : {
                name : '港聞',
            },
            國際 : {
                name : '國際',
            },
            兩岸 : {
                name : '兩岸',
            },
            經濟 : {
                name : '經濟',
            },
            地產 : {
                name : '地產',
            },
            娛樂 : {
                name : '娛樂',
            },
            副刊 : {
                name : '副刊',
            },
            體育 : {
                name : '體育',
            },
        },
    },
    english : {
        ...englishMessages,
        labels    : {
            read_more : 'Read more',
            publisher : 'Publisher',
        },
        resources : {
            港聞 : {
                name : 'Local',
            },
            國際 : {
                name : 'World',
            },
            兩岸 : {
                name : 'China',
            },
            經濟 : {
                name : 'Economy',
            },
            地產 : {
                name : 'Property',
            },
            娛樂 : {
                name : 'Entertainment',
            },
            副刊 : {
                name : 'Supplement',
            },
            體育 : {
                name : 'Sports',
            },
        },
    },
};
