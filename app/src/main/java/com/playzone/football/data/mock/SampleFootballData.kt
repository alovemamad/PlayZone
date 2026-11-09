package com.playzone.football.data.mock

import com.playzone.football.data.model.League
import com.playzone.football.data.model.Match
import com.playzone.football.data.model.MatchEvent
import com.playzone.football.data.model.MatchStats
import com.playzone.football.data.model.MatchStatus
import com.playzone.football.data.model.Team
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object SampleFootballData {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayDate(): String = dateFormat.format(Date())
    fun getTomorrowDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        return dateFormat.format(cal.time)
    }
    fun getYesterdayDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(cal.time)
    }

    val sampleLeagues = listOf(
        League(
            id = "league_ucl",
            name = "UEFA Champions League",
            persianName = "لیگ قهرمانان اروپا",
            country = "اروپا",
            flagEmoji = "🇪🇺",
            logoUrl = "https://media.api-sports.io/football/leagues/2.png",
            isHot = true
        ),
        League(
            id = "league_epl",
            name = "Premier League",
            persianName = "لیگ برتر انگلیس",
            country = "انگلستان",
            flagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
            logoUrl = "https://media.api-sports.io/football/leagues/39.png",
            isHot = true
        ),
        League(
            id = "league_laliga",
            name = "La Liga",
            persianName = "لالیگا اسپانیا",
            country = "اسپانیا",
            flagEmoji = "🇪🇸",
            logoUrl = "https://media.api-sports.io/football/leagues/140.png",
            isHot = true
        ),
        League(
            id = "league_persian",
            name = "Persian Gulf Pro League",
            persianName = "لیگ برتر خلیج فارس",
            country = "ایران",
            flagEmoji = "🇮🇷",
            logoUrl = "https://media.api-sports.io/football/leagues/291.png",
            isHot = true
        ),
        League(
            id = "league_seriea",
            name = "Serie A",
            persianName = "سری آ ایتالیا",
            country = "ایتالیا",
            flagEmoji = "🇮🇹",
            logoUrl = "https://media.api-sports.io/football/leagues/135.png"
        ),
        League(
            id = "league_bundesliga",
            name = "Bundesliga",
            persianName = "بوندس‌لیگا آلمان",
            country = "آلمان",
            flagEmoji = "🇩🇪",
            logoUrl = "https://media.api-sports.io/football/leagues/78.png"
        ),
        League(
            id = "league_ligue1",
            name = "Ligue 1",
            persianName = "لیگ ۱ فرانسه",
            country = "فرانسه",
            flagEmoji = "🇫🇷",
            logoUrl = "https://media.api-sports.io/football/leagues/61.png"
        )
    )

    val sampleTeams = listOf(
        // EPL
        Team("team_mancity", "Manchester City", "منچسترسیتی", "league_epl", "https://media.api-sports.io/football/teams/50.png", 1, 28, 20, 5, 3, 67, 24, 65, listOf("W", "W", "W", "D", "W")),
        Team("team_liverpool", "Liverpool", "لیورپول", "league_epl", "https://media.api-sports.io/football/teams/40.png", 2, 28, 19, 6, 3, 64, 25, 63, listOf("W", "D", "W", "W", "W")),
        Team("team_arsenal", "Arsenal", "آرسنال", "league_epl", "https://media.api-sports.io/football/teams/42.png", 3, 28, 18, 7, 3, 60, 22, 61, listOf("W", "W", "D", "W", "D")),
        Team("team_chelsea", "Chelsea", "چلسی", "league_epl", "https://media.api-sports.io/football/teams/49.png", 4, 28, 15, 6, 7, 51, 35, 51, listOf("W", "L", "W", "W", "D")),
        Team("team_tottenham", "Tottenham Hotspur", "تاتنهام", "league_epl", "https://media.api-sports.io/football/teams/47.png", 5, 28, 14, 5, 9, 53, 40, 47, listOf("L", "W", "D", "W", "L")),
        Team("team_manunited", "Manchester United", "منچستریونایتد", "league_epl", "https://media.api-sports.io/football/teams/33.png", 6, 28, 13, 5, 10, 42, 39, 44, listOf("W", "D", "L", "W", "W")),

        // La Liga
        Team("team_realmadrid", "Real Madrid", "رئال مادرید", "league_laliga", "https://media.api-sports.io/football/teams/541.png", 1, 27, 21, 4, 2, 62, 18, 67, listOf("W", "W", "W", "W", "D")),
        Team("team_barcelona", "Barcelona", "بارسلونا", "league_laliga", "https://media.api-sports.io/football/teams/529.png", 2, 27, 19, 5, 3, 65, 28, 62, listOf("W", "W", "W", "L", "W")),
        Team("team_atletico", "Atletico Madrid", "اتلتیکو مادرید", "league_laliga", "https://media.api-sports.io/football/teams/530.png", 3, 27, 16, 6, 5, 48, 26, 54, listOf("D", "W", "W", "D", "W")),
        Team("team_girona", "Girona", "خیرونا", "league_laliga", "https://media.api-sports.io/football/teams/547.png", 4, 27, 15, 5, 7, 45, 32, 50, listOf("W", "L", "W", "L", "W")),

        // Persian Gulf Pro League
        Team("team_persepolis", "Persepolis FC", "پرسپولیس تهران", "league_persian", "https://media.api-sports.io/football/teams/3522.png", 1, 23, 16, 5, 2, 40, 12, 53, listOf("W", "W", "D", "W", "W")),
        Team("team_esteghlal", "Esteghlal FC", "استقلال تهران", "league_persian", "https://media.api-sports.io/football/teams/3521.png", 2, 23, 15, 6, 2, 36, 14, 51, listOf("W", "D", "W", "W", "D")),
        Team("team_sepahan", "Sepahan SC", "سپاهان اصفهان", "league_persian", "https://media.api-sports.io/football/teams/3523.png", 3, 23, 14, 4, 5, 44, 21, 46, listOf("L", "W", "W", "D", "W")),
        Team("team_tractor", "Tractor SC", "تراکتور تبریز", "league_persian", "https://media.api-sports.io/football/teams/3524.png", 4, 23, 13, 5, 5, 39, 18, 44, listOf("W", "W", "L", "W", "W")),
        Team("team_foolad", "Foolad FC", "فولاد خوزستان", "league_persian", "https://media.api-sports.io/football/teams/3525.png", 5, 23, 10, 7, 6, 28, 22, 37, listOf("D", "W", "D", "L", "W")),
        Team("team_golgohar", "Gol Gohar Sirjan", "گل‌گهر سیرجان", "league_persian", "https://media.api-sports.io/football/teams/3526.png", 6, 23, 9, 8, 6, 25, 20, 35, listOf("D", "D", "W", "D", "L")),
        Team("team_malavan", "Malavan Bandar Anzali", "ملوان انزلی", "league_persian", "https://media.api-sports.io/football/teams/3527.png", 7, 23, 9, 7, 7, 26, 22, 34, listOf("W", "L", "D", "W", "D")),
        Team("team_zobahan", "Zob Ahan Isfahan", "ذوب‌آهن اصفهان", "league_persian", "https://media.api-sports.io/football/teams/3528.png", 8, 23, 8, 9, 6, 24, 23, 33, listOf("D", "W", "L", "D", "D")),

        // Champions League / Europe
        Team("team_bayern", "Bayern Munich", "بایرن مونیخ", "league_bundesliga", "https://media.api-sports.io/football/teams/157.png", 1, 25, 19, 3, 3, 71, 29, 60, listOf("W", "W", "W", "W", "D")),
        Team("team_leverkusen", "Bayer Leverkusen", "بایر لورکوزن", "league_bundesliga", "https://media.api-sports.io/football/teams/168.png", 2, 25, 17, 6, 2, 60, 24, 57, listOf("W", "W", "D", "W", "W")),
        Team("team_dortmund", "Borussia Dortmund", "دورتموند", "league_bundesliga", "https://media.api-sports.io/football/teams/165.png", 3, 25, 15, 5, 5, 52, 31, 50, listOf("W", "L", "W", "W", "D")),
        Team("team_inter", "Inter Milan", "اینتر میلان", "league_seriea", "https://media.api-sports.io/football/teams/505.png", 1, 27, 22, 3, 2, 66, 16, 69, listOf("W", "W", "W", "W", "W")),
        Team("team_juventus", "Juventus", "یوونتوس", "league_seriea", "https://media.api-sports.io/football/teams/496.png", 2, 27, 18, 5, 4, 46, 20, 59, listOf("W", "D", "W", "W", "L")),
        Team("team_milan", "AC Milan", "آث میلان", "league_seriea", "https://media.api-sports.io/football/teams/489.png", 3, 27, 17, 6, 4, 54, 30, 57, listOf("W", "W", "L", "W", "D")),
        Team("team_psg", "Paris Saint-Germain", "پاری‌سن‌ژرمن", "league_ligue1", "https://media.api-sports.io/football/teams/85.png", 1, 25, 18, 5, 2, 63, 21, 59, listOf("W", "W", "W", "D", "W"))
    )

    fun createInitialMatches(): List<Match> {
        val today = getTodayDate()
        val tomorrow = getTomorrowDate()
        val yesterday = getYesterdayDate()

        return listOf(
            // === LIVE MATCHES (در حال برگزاری - شبیه‌ساز زنده با دقایق، رویدادها و آمار کامل) ===
            Match(
                id = "live_ucl_1",
                leagueId = "league_ucl",
                leagueName = "UEFA Champions League",
                leaguePersianName = "لیگ قهرمانان اروپا",
                homeTeamId = "team_realmadrid",
                homeTeamName = "Real Madrid",
                homeTeamPersian = "رئال مادرید",
                homeTeamLogo = "https://media.api-sports.io/football/teams/541.png",
                awayTeamId = "team_mancity",
                awayTeamName = "Manchester City",
                awayTeamPersian = "منچسترسیتی",
                awayTeamLogo = "https://media.api-sports.io/football/teams/50.png",
                status = MatchStatus.LIVE,
                minute = 73,
                homeScore = 2,
                awayScore = 2,
                matchTime = "۲۲:۳۰",
                matchDate = today,
                stadium = "ورزشگاه سانتیاگو برنابئو، مادرید",
                referee = "شیمون مارسینیاک",
                simHomeProb = 36,
                simDrawProb = 33,
                simAwayProb = 31,
                simOverProb = 68,
                simUnderProb = 32,
                stats = MatchStats(51, 49, 7, 6, 8, 5, 12, 10, 2, 1),
                events = listOf(
                    MatchEvent(14, "GOAL", "منچسترسیتی", "ارلینگ هالند", "ضربه سر محکم روی سانتر دی‌بروینه"),
                    MatchEvent(32, "GOAL", "رئال مادرید", "وینیسیوس جونیور", "نفوذ تماشایی از جناح چپ و شوت زاویه بسته"),
                    MatchEvent(41, "CARD_YELLOW", "منچسترسیتی", "رودری", "خطای تاکتیکی در میانه میدان"),
                    MatchEvent(56, "GOAL", "رئال مادرید", "جود بلینگام", "ضربه پای چپ ریباند"),
                    MatchEvent(65, "GOAL", "منچسترسیتی", "کوین دی‌بروینه", "شوت تماشایی از پشت محوطه جریمه")
                )
            ),
            Match(
                id = "live_persian_1",
                leagueId = "league_persian",
                leagueName = "Persian Gulf Pro League",
                leaguePersianName = "لیگ برتر خلیج فارس",
                homeTeamId = "team_persepolis",
                homeTeamName = "Persepolis FC",
                homeTeamPersian = "پرسپولیس تهران",
                homeTeamLogo = "https://media.api-sports.io/football/teams/3522.png",
                awayTeamId = "team_esteghlal",
                awayTeamName = "Esteghlal FC",
                awayTeamPersian = "استقلال تهران",
                awayTeamLogo = "https://media.api-sports.io/football/teams/3521.png",
                status = MatchStatus.LIVE,
                minute = 61,
                homeScore = 1,
                awayScore = 0,
                matchTime = "۱۷:۳۰",
                matchDate = today,
                stadium = "ورزشگاه بزرگ آزادی، تهران",
                referee = "موعود بنیادی‌فر",
                simHomeProb = 48,
                simDrawProb = 32,
                simAwayProb = 20,
                simOverProb = 40,
                simUnderProb = 60,
                stats = MatchStats(56, 44, 5, 3, 6, 3, 14, 16, 2, 3),
                events = listOf(
                    MatchEvent(19, "CARD_YELLOW", "استقلال تهران", "روزبه چشمی", "تکل روی مهاجم حریف"),
                    MatchEvent(38, "GOAL", "پرسپولیس تهران", "اوستون اورونوف", "دریبل زیبا و شوت پای راست به کنج دروازه"),
                    MatchEvent(52, "CARD_YELLOW", "پرسپولیس تهران", "سروش رفیعی", "اعتراض به تصمیم داور")
                )
            ),
            Match(
                id = "live_epl_1",
                leagueId = "league_epl",
                leagueName = "Premier League",
                leaguePersianName = "لیگ برتر انگلیس",
                homeTeamId = "team_arsenal",
                homeTeamName = "Arsenal",
                homeTeamPersian = "آرسنال",
                homeTeamLogo = "https://media.api-sports.io/football/teams/42.png",
                awayTeamId = "team_chelsea",
                awayTeamName = "Chelsea",
                awayTeamPersian = "چلسی",
                awayTeamLogo = "https://media.api-sports.io/football/teams/49.png",
                status = MatchStatus.LIVE,
                minute = 34,
                homeScore = 1,
                awayScore = 1,
                matchTime = "۲۰:۰۰",
                matchDate = today,
                stadium = "ورزشگاه امارات، لندن",
                referee = "آنتونی تیلور",
                simHomeProb = 45,
                simDrawProb = 30,
                simAwayProb = 25,
                simOverProb = 62,
                simUnderProb = 38,
                stats = MatchStats(58, 42, 4, 3, 5, 2, 8, 9, 1, 1),
                events = listOf(
                    MatchEvent(9, "GOAL", "آرسنال", "بوکایو ساکا", "نفوذ سریع و شوت به زاویه مخالف"),
                    MatchEvent(27, "GOAL", "چلسی", "کول پالمر", "ضربه پنالتی دقیق")
                )
            ),
            Match(
                id = "live_seriea_1",
                leagueId = "league_seriea",
                leagueName = "Serie A",
                leaguePersianName = "سری آ ایتالیا",
                homeTeamId = "team_inter",
                homeTeamName = "Inter Milan",
                homeTeamPersian = "اینتر میلان",
                homeTeamLogo = "https://media.api-sports.io/football/teams/505.png",
                awayTeamId = "team_juventus",
                awayTeamName = "Juventus",
                awayTeamPersian = "یوونتوس",
                awayTeamLogo = "https://media.api-sports.io/football/teams/496.png",
                status = MatchStatus.LIVE,
                minute = 82,
                homeScore = 2,
                awayScore = 1,
                matchTime = "۲۱:۱۵",
                matchDate = today,
                stadium = "ورزشگاه سن سیرو، میلان",
                referee = "دنیله اورساتو",
                simHomeProb = 52,
                simDrawProb = 28,
                simAwayProb = 20,
                simOverProb = 50,
                simUnderProb = 50,
                stats = MatchStats(54, 46, 6, 4, 7, 4, 11, 14, 2, 3),
                events = listOf(
                    MatchEvent(21, "GOAL", "اینتر میلان", "لائوتارو مارتینز", "ضربه سر زیبا"),
                    MatchEvent(58, "GOAL", "یوونتوس", "دوشان ولاهوویچ", "شوت از داخل محوطه جریمه"),
                    MatchEvent(71, "GOAL", "اینتر میلان", "مارکوس تورام", "فرار عالی در ضدحمله")
                )
            ),

            // === UPCOMING MATCHES (پیش‌رو برای امروز و فردا با ضرایب کامل 1xBet) ===
            Match(
                id = "upcoming_laliga_1",
                leagueId = "league_laliga",
                leagueName = "La Liga",
                leaguePersianName = "لالیگا اسپانیا",
                homeTeamId = "team_barcelona",
                homeTeamName = "Barcelona",
                homeTeamPersian = "بارسلونا",
                homeTeamLogo = "https://media.api-sports.io/football/teams/529.png",
                awayTeamId = "team_atletico",
                awayTeamName = "Atletico Madrid",
                awayTeamPersian = "اتلتیکو مادرید",
                awayTeamLogo = "https://media.api-sports.io/football/teams/530.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۲۳:۳۰",
                matchDate = today,
                stadium = "ورزشگاه المپیک لوئیس کمپانی",
                referee = "خسوس خیل مانزانو",
                simHomeProb = 48,
                simDrawProb = 27,
                simAwayProb = 25,
                simOverProb = 58,
                simUnderProb = 42
            ),
            Match(
                id = "upcoming_epl_2",
                leagueId = "league_epl",
                leagueName = "Premier League",
                leaguePersianName = "لیگ برتر انگلیس",
                homeTeamId = "team_liverpool",
                homeTeamName = "Liverpool",
                homeTeamPersian = "لیورپول",
                homeTeamLogo = "https://media.api-sports.io/football/teams/40.png",
                awayTeamId = "team_manunited",
                awayTeamName = "Manchester United",
                awayTeamPersian = "منچستریونایتد",
                awayTeamLogo = "https://media.api-sports.io/football/teams/33.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۱۹:۰۰",
                matchDate = tomorrow,
                stadium = "ورزشگاه آنفیلد، لیورپول",
                referee = "مایکل اولیور",
                simHomeProb = 58,
                simDrawProb = 24,
                simAwayProb = 18,
                simOverProb = 64,
                simUnderProb = 36
            ),
            Match(
                id = "upcoming_persian_2",
                leagueId = "league_persian",
                leagueName = "Persian Gulf Pro League",
                leaguePersianName = "لیگ برتر خلیج فارس",
                homeTeamId = "team_sepahan",
                homeTeamName = "Sepahan SC",
                homeTeamPersian = "سپاهان اصفهان",
                homeTeamLogo = "https://media.api-sports.io/football/teams/3523.png",
                awayTeamId = "team_tractor",
                awayTeamName = "Tractor SC",
                awayTeamPersian = "تراکتور تبریز",
                awayTeamLogo = "https://media.api-sports.io/football/teams/3524.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۱۶:۳۰",
                matchDate = tomorrow,
                stadium = "ورزشگاه نقش جهان اصفهان",
                referee = "بیژن حیدری",
                simHomeProb = 42,
                simDrawProb = 33,
                simAwayProb = 25,
                simOverProb = 46,
                simUnderProb = 54
            ),
            Match(
                id = "upcoming_bundesliga_1",
                leagueId = "league_bundesliga",
                leagueName = "Bundesliga",
                leaguePersianName = "بوندس‌لیگا آلمان",
                homeTeamId = "team_bayern",
                homeTeamName = "Bayern Munich",
                homeTeamPersian = "بایرن مونیخ",
                homeTeamLogo = "https://media.api-sports.io/football/teams/157.png",
                awayTeamId = "team_leverkusen",
                awayTeamName = "Bayer Leverkusen",
                awayTeamPersian = "بایر لورکوزن",
                awayTeamLogo = "https://media.api-sports.io/football/teams/168.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۲۱:۰۰",
                matchDate = tomorrow,
                stadium = "ورزشگاه آلیانتس آرنا، مونیخ",
                referee = "فلیکس زوایر",
                simHomeProb = 51,
                simDrawProb = 26,
                simAwayProb = 23,
                simOverProb = 68,
                simUnderProb = 32
            ),
            Match(
                id = "upcoming_ucl_2",
                leagueId = "league_ucl",
                leagueName = "UEFA Champions League",
                leaguePersianName = "لیگ قهرمانان اروپا",
                homeTeamId = "team_psg",
                homeTeamName = "Paris Saint-Germain",
                homeTeamPersian = "پاری‌سن‌ژرمن",
                homeTeamLogo = "https://media.api-sports.io/football/teams/85.png",
                awayTeamId = "team_dortmund",
                awayTeamName = "Borussia Dortmund",
                awayTeamPersian = "دورتموند",
                awayTeamLogo = "https://media.api-sports.io/football/teams/165.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۲۳:۱۵",
                matchDate = tomorrow,
                stadium = "ورزشگاه پارک د پرنس، پاریس",
                referee = "کلمنت تورپین",
                simHomeProb = 55,
                simDrawProb = 25,
                simAwayProb = 20,
                simOverProb = 65,
                simUnderProb = 35
            ),
            Match(
                id = "upcoming_persian_3",
                leagueId = "league_persian",
                leagueName = "Persian Gulf Pro League",
                leaguePersianName = "لیگ برتر خلیج فارس",
                homeTeamId = "team_foolad",
                homeTeamName = "Foolad FC",
                homeTeamPersian = "فولاد خوزستان",
                homeTeamLogo = "https://media.api-sports.io/football/teams/3525.png",
                awayTeamId = "team_malavan",
                awayTeamName = "Malavan",
                awayTeamPersian = "ملوان انزلی",
                awayTeamLogo = "https://media.api-sports.io/football/teams/3527.png",
                status = MatchStatus.UPCOMING,
                minute = 0,
                homeScore = 0,
                awayScore = 0,
                matchTime = "۱۸:۰۰",
                matchDate = tomorrow,
                stadium = "ورزشگاه فولاد آرنا، اهواز",
                referee = "پیام حیدری",
                simHomeProb = 38,
                simDrawProb = 35,
                simAwayProb = 27,
                simOverProb = 35,
                simUnderProb = 65
            ),

            // === FINISHED MATCHES (پایان‌یافته با نتایج قطعی) ===
            Match(
                id = "fin_epl_1",
                leagueId = "league_epl",
                leagueName = "Premier League",
                leaguePersianName = "لیگ برتر انگلیس",
                homeTeamId = "team_mancity",
                homeTeamName = "Manchester City",
                homeTeamPersian = "منچسترسیتی",
                homeTeamLogo = "https://media.api-sports.io/football/teams/50.png",
                awayTeamId = "team_tottenham",
                awayTeamName = "Tottenham Hotspur",
                awayTeamPersian = "تاتنهام",
                awayTeamLogo = "https://media.api-sports.io/football/teams/47.png",
                status = MatchStatus.FINISHED,
                minute = 90,
                homeScore = 3,
                awayScore = 1,
                matchTime = "پایان",
                matchDate = yesterday,
                stadium = "ورزشگاه اتحاد، منچستر",
                referee = "کریس کاوانا",
                simHomeProb = 56,
                simDrawProb = 24,
                simAwayProb = 20,
                simOverProb = 65,
                simUnderProb = 35,
                stats = MatchStats(62, 38, 9, 3, 8, 2, 7, 12, 1, 2),
                events = listOf(
                    MatchEvent(18, "GOAL", "منچسترسیتی", "فیل فودن", "شوت دقیق زمینی"),
                    MatchEvent(43, "GOAL", "تاتنهام", "سون هیونگ مین", "ضدحمله برق‌آسا"),
                    MatchEvent(67, "GOAL", "منچسترسیتی", "ارلینگ هالند", "ضربه سر چکشی"),
                    MatchEvent(89, "GOAL", "منچسترسیتی", "برناردو سیلوا", "پاس‌کاری تکنیکی در محوطه")
                )
            ),
            Match(
                id = "fin_persian_1",
                leagueId = "league_persian",
                leagueName = "Persian Gulf Pro League",
                leaguePersianName = "لیگ برتر خلیج فارس",
                homeTeamId = "team_tractor",
                homeTeamName = "Tractor SC",
                homeTeamPersian = "تراکتور تبریز",
                homeTeamLogo = "https://media.api-sports.io/football/teams/3524.png",
                awayTeamId = "team_golgohar",
                awayTeamName = "Gol Gohar Sirjan",
                awayTeamPersian = "گل‌گهر سیرجان",
                awayTeamLogo = "https://media.api-sports.io/football/teams/3526.png",
                status = MatchStatus.FINISHED,
                minute = 90,
                homeScore = 2,
                awayScore = 0,
                matchTime = "پایان",
                matchDate = yesterday,
                stadium = "ورزشگاه یادگار امام تبریز",
                referee = "امیر عرب‌براقی",
                simHomeProb = 49,
                simDrawProb = 31,
                simAwayProb = 20,
                simOverProb = 45,
                simUnderProb = 55,
                stats = MatchStats(55, 45, 6, 2, 5, 3, 12, 15, 2, 2),
                events = listOf(
                    MatchEvent(29, "GOAL", "تراکتور تبریز", "امیرحسین حسین‌زاده", "شوت فنی زیبا"),
                    MatchEvent(84, "GOAL", "تراکتور تبریز", "تومیسلاو اشترکالی", "ضربه تمام‌کننده تک به تک")
                )
            )
        )
    }
}
