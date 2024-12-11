import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import os
from dotenv import load_dotenv


load_dotenv()


cred = credentials.Certificate(os.getenv("FIREBASE_ADMIN_SDK_PATH"))
firebase_admin.initialize_app(cred, {
    'databaseURL': os.getenv("FIREBASE_DATABASE_URL")
})


def read_and_delete_data():
    try:

        ref = db.reference('wifi_info')
        wifi_data = ref.get()

        if wifi_data:
            for key, data in wifi_data.items():

                timestamp = data.get('timestamp', 'N/A')
                ssid = data.get('ssid', 'N/A')
                signal_level = data.get('signal_level', 'N/A')


                print(f"時間戳: {timestamp}")
                print(f"SSID: {ssid}")
                print(f"信號強度: {signal_level}")
                print("------------------------")

                # delete
                ref.child(key).delete()
            
            print("所有數據已被讀取並刪除。")
        else:
            print("數據庫中沒有找到數據。")
    except Exception as e:
        print(f"發生錯誤: {e}")

if __name__ == "__main__":
    read_and_delete_data()