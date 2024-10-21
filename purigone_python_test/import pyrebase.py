import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import os
from dotenv import load_dotenv

# 載入環境變數
load_dotenv()

# Firebase 配置
cred = credentials.Certificate(os.getenv("FIREBASE_ADMIN_SDK_PATH"))
firebase_admin.initialize_app(cred, {
    'databaseURL': os.getenv("FIREBASE_DATABASE_URL")
})

# 讀取和刪除數據的函數
def read_and_delete_data():
    try:
        # 從 'wifi_info' 節點讀取所有數據
        ref = db.reference('wifi_info')
        wifi_data = ref.get()

        if wifi_data:
            for key, data in wifi_data.items():
                # 提取三個變數
                timestamp = data.get('timestamp', 'N/A')
                ssid = data.get('ssid', 'N/A')
                signal_level = data.get('signal_level', 'N/A')

                # 打印數據
                print(f"時間戳: {timestamp}")
                print(f"SSID: {ssid}")
                print(f"信號強度: {signal_level}")
                print("------------------------")

                # 刪除數據
                ref.child(key).delete()
            
            print("所有數據已被讀取並刪除。")
        else:
            print("數據庫中沒有找到數據。")
    except Exception as e:
        print(f"發生錯誤: {e}")

# 運行函數
if __name__ == "__main__":
    read_and_delete_data()