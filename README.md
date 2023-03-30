<img style="width:64px" src="https://i.imgur.com/r6MpthL.png" />

# 水國大王
> Fruit King

## 介紹影片
<a href="http://www.youtube.com/watch?v=KGMi2AdwXTE"><img title="點擊觀看！" src="https://i.imgur.com/aVQbyIc.png" height="250" ></a>

## 動機
現有的水果價格查詢應用，需要消費者逐一輸入水果名稱，不方便且低效。為解決此問題，參考 `Google Lens` 相機，使用圖像辨識水果，結合即時的水果行情資料，使消費者只需拍照，即可查詢多種水果價格，讓購物更簡單、智慧。
 	
## 介紹
> 以`TFLite`框架在 Android 上部屬影像分類模型，以政府提供的蔬果交易價格為基準，查詢各類水果價格。

<img style="width:500px" src="https://i.imgur.com/FzouYIB.png" />

1. 主選單: 顯示當季水果列表，提醒使用者，當季水果的價格浮動較小，可以優先選購，按下辨識將進入拍攝頁面

2. 拍照辨識: 要求拍攝權限，開始拍照

3. 均價顯示: 根據辨識結果顯示價格

## 可行性
  使用 tflite 辨識框架[^1]，使用`Teachable Machine`[^2]的模型訓練平台，訓練圖像辨識模型，根據政府提供的蔬果的交易價格 API[^3]，及時更新水果均價。

#### MediaPipe 訓練畫面
<img style="width:400px" src="https://i.imgur.com/MOdG6mr.png" />

[^1]:Yanwei Liu, 作者, 「[TensorFlow Lite 學習筆記](https://yanwei-liu.medium.com/tensorflow-lite%E5%AD%B8%E7%BF%92%E7%AD%86%E8%A8%98-c95e12f97b9a)」. 引見於: 2023年03月30日.
[^2]:「[Teachable Machine](https://teachablemachine.withgoogle.com/)」. (引見於 2023年03月30日).
[^3]:行政院資料開放平台, 單位, 「[農產品交易行情](https://data.coa.gov.tw/api.aspx#operations-tag-%E4%BA%A4%E6%98%93%E8%A1%8C%E6%83%85)」. (引見於 2023年03月30日).
