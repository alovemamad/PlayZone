# PlayZone – virtual-money football betting simulator

Real fixtures, real bookmaker odds (API-Football, bookmaker 8), automatic settlement from the
official final score. Only the money is virtual.

## Run
1. Put your key in `.env` (see `.env.example`): `API_FOOTBALL_KEY=...`
2. Open in Android Studio, run.

## How it works
- Fixtures today + tomorrow, pre-match odds (1X2, double chance, over/under 1.5 & 2.5, BTTS),
  live scores, live odds (best effort), events and statistics on the match page.
- Missing odds are shown as "—" and cannot be bet on. No made-up prices in real mode.
- Pending bets are settled automatically on the 90-minute result; postponed/cancelled matches refund.
- Wallet: any balance, recharge any time.
- Free plan = 100 requests/day: live refresh every 10 minutes. Paid plan: every minute.
