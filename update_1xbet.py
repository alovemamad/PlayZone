import json
import urllib.request
import urllib.parse
import os

def get_live_football():
    # آدرس API 1xbet برای مسابقات زنده فوتبال
    base_url = "https://1xbet.com/LiveFeed/Get1x2_Zip"
    params = {
        "getEmpty": "true",
        "count": "500",
        "lng": "fa",
        "sports": "1" # 1 = فوتبال
    }
    
    url = base_url + "?" + urllib.parse.urlencode(params)
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
        'Accept': 'application/json'
    }
    
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req, timeout=15) as response:
            data = json.loads(response.read().decode('utf-8'))
            
        matches = []
        if 'Value' in data:
            for game in data['Value']:
                match_data = {
                    "id": str(game.get('I')),
                    "league": game.get('L', 'لیگ ناشناس'),
                    "home_team": game.get('O1', 'تیم ۱'),
                    "away_team": game.get('O2', 'تیم ۲'),
                    "score_home": game.get('SC', {}).get('FS', {}).get('S1', 0),
                    "score_away": game.get('SC', {}).get('FS', {}).get('S2', 0),
                    "time": game.get('SC', {}).get('TS', 0),
                    "status": "زنده",
                    "odds": {
                        "home": next((x['C'] for x in game.get('E', []) if x.get('T') == 1), 0),
                        "draw": next((x['C'] for x in game.get('E', []) if x.get('T') == 2), 0),
                        "away": next((x['C'] for x in game.get('E', []) if x.get('T') == 3), 0)
                    }
                }
                matches.append(match_data)
        
        # ساخت پوشه assets اگر وجود نداشته باشد
        os.makedirs("app/src/main/assets", exist_ok=True)
                
        with open("app/src/main/assets/live_matches.json", "w", encoding="utf-8") as f:
            json.dump(matches, f, ensure_ascii=False, indent=4)
            
        print(f"Successfully saved {len(matches)} live matches.")
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    get_live_football()
