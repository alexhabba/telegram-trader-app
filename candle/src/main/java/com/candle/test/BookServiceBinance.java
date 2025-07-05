package com.candle.test;

import com.candle.BookInfoDto;
import com.candle.book.BinanceSymbolsFetcher;
import com.dao.bot.entity.Candle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.candle.test.CandleApi.getCandleWithoutTicks;
import static com.candle.test.Utils.getResponse;
import static java.lang.Math.max;
import static java.util.Objects.isNull;

public class BookServiceBinance {

    private static final Set<String> setSymbolExcludes = Set.of(
            "FDUSDT",
            "USDCUSDT",
            "ETHUSDT",
            "BTCUSDT"
    );
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {
        BinanceSymbolsFetcher binanceSymbolsFetcher = new BinanceSymbolsFetcher();
        // USDT, ETH, BTC
        List<String> symbols = binanceSymbolsFetcher.getAllSymbol("USDT");
        List<BookInfoDto> list = new ArrayList<>();
//        symbols.forEach(s -> {
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            BookInfoDto result = getInfoBookBySymbol(s);
//            System.out.println(atomicInteger.addAndGet(1) + "    " + s + "    " + result);
//
//            List<Bar> candle = getCandle(Symbol.ZEN, LocalDateTime.now().minusDays(20));
//            System.out.println(candle);
//
//        });
        System.out.println(LocalDateTime.now());

        symbols.stream()
                .filter(sym -> !setSymbolExcludes.contains(sym))
                .forEach(s -> {
                    int limit = 10;
                    List<Candle> candle = getCandleWithoutTicks(s, LocalDateTime.now().minusHours(3).minusMinutes(65), "5m");
                    List<Candle> collect = candle.stream().limit(limit).collect(Collectors.toList());

                    // нужно не считать последние 3 свечи
                    double medianVolume = collect.stream()
                            .mapToDouble(Candle::getVol)
                            .sum() / collect.size() * 3;

                    double vol0 = candle.get(candle.size() - 1).getVol();
                    double vol1 = candle.get(candle.size() - 2).getVol();
                    double vol2 = candle.get(candle.size() - 3).getVol();

                    double pr0 = candle.get(candle.size() - 1).getClose();
                    int maxVolInUsdt = (int) (max(vol0, max(vol1, vol2)) * pr0);
                    int bigVol = 200000;
                    boolean checkSum = maxVolInUsdt > bigVol;

                    if (candle.size() >= limit && checkSum && (medianVolume < vol0 || medianVolume < vol1 || medianVolume < vol2)) {
                        String symb = s.replace("USDT", "_USDT");
//                https://www.binance.com/ru/trade/ZEN_USDT?type=spot
                        System.out.println("https://www.binance.com/ru/trade/" + symb + "?type=spot     " + maxVolInUsdt);
                    }
                });
//                EURUSDT
//2025-05-30T08:40:29.057369

//        Bar(id=null, createDate=2025-05-23T00:00, symbol=ZEN, volBuy=25.64, volSell=0.0, vol=670729.32, open=10.46, close=9.36, low=9.33, high=10.89, interval=1)

//        List<Candle> candle = getCandleWithoutTicks("ZENUSDT", LocalDateTime.now().minusDays(20));
        // находим средний объем за 20 дней

//        double medianVolume = candle.stream()
//                .mapToDouble(Candle::getVol)
//                .sum() / candle.size();
        System.out.println(LocalDateTime.now());
        System.out.println("========================");

        System.out.println("============end=============");
//        DOGEUSDT
//        35    DOGEUSDT    CandleApi.ResponseDto(symbol=DOGEUSDT, bids=[CandleApi.PriceQtyInfo(price=0.22616, qty=329341.0, qtyUsdt=74483), CandleApi.PriceQtyInfo(price=0.22611, qty=413792.0, qtyUsdt=93562), CandleApi.PriceQtyInfo(price=0.22598, qty=453784.0, qtyUsdt=102546), CandleApi.PriceQtyInfo(price=0.2259, qty=490985.0, qtyUsdt=110913), CandleApi.PriceQtyInfo(price=0.22464, qty=402510.0, qtyUsdt=90419), CandleApi.PriceQtyInfo(price=0.22448, qty=699472.0, qtyUsdt=157017), CandleApi.PriceQtyInfo(price=0.22431, qty=1032323.0, qtyUsdt=231560), CandleApi.PriceQtyInfo(price=0.22414, qty=1391168.0, qtyUsdt=311816), CandleApi.PriceQtyInfo(price=0.22399, qty=380765.0, qtyUsdt=85287), CandleApi.PriceQtyInfo(price=0.22386, qty=1666284.0, qtyUsdt=373014), CandleApi.PriceQtyInfo(price=0.22318, qty=1360316.0, qtyUsdt=303595), CandleApi.PriceQtyInfo(price=0.22205, qty=876156.0, qtyUsdt=194550), CandleApi.PriceQtyInfo(price=0.22036, qty=343492.0, qtyUsdt=75691), CandleApi.PriceQtyInfo(price=0.22, qty=646546.0, qtyUsdt=142240), CandleApi.PriceQtyInfo(price=0.21935, qty=835640.0, qtyUsdt=183297), CandleApi.PriceQtyInfo(price=0.21867, qty=347878.0, qtyUsdt=76070), CandleApi.PriceQtyInfo(price=0.2185, qty=1578657.0, qtyUsdt=344936), CandleApi.PriceQtyInfo(price=0.218, qty=338552.0, qtyUsdt=73804), CandleApi.PriceQtyInfo(price=0.216, qty=407490.0, qtyUsdt=88017), CandleApi.PriceQtyInfo(price=0.215, qty=2091050.0, qtyUsdt=449575), CandleApi.PriceQtyInfo(price=0.21258, qty=368946.0, qtyUsdt=78430), CandleApi.PriceQtyInfo(price=0.21111, qty=492066.0, qtyUsdt=103880), CandleApi.PriceQtyInfo(price=0.211, qty=369624.0, qtyUsdt=77990), CandleApi.PriceQtyInfo(price=0.21, qty=2609511.0, qtyUsdt=547997), CandleApi.PriceQtyInfo(price=0.2094, qty=448094.0, qtyUsdt=93830), CandleApi.PriceQtyInfo(price=0.209, qty=410117.0, qtyUsdt=85714), CandleApi.PriceQtyInfo(price=0.20888, qty=439897.0, qtyUsdt=91885), CandleApi.PriceQtyInfo(price=0.20725, qty=400581.0, qtyUsdt=83020), CandleApi.PriceQtyInfo(price=0.2068, qty=1564751.0, qtyUsdt=323590), CandleApi.PriceQtyInfo(price=0.206, qty=490591.0, qtyUsdt=101061), CandleApi.PriceQtyInfo(price=0.20545, qty=974144.0, qtyUsdt=200137), CandleApi.PriceQtyInfo(price=0.20538, qty=576039.0, qtyUsdt=118306), CandleApi.PriceQtyInfo(price=0.2052, qty=1172438.0, qtyUsdt=240584), CandleApi.PriceQtyInfo(price=0.205, qty=1328132.0, qtyUsdt=272267), CandleApi.PriceQtyInfo(price=0.2042, qty=1083749.0, qtyUsdt=221301), CandleApi.PriceQtyInfo(price=0.20333, qty=500702.0, qtyUsdt=101807), CandleApi.PriceQtyInfo(price=0.20021, qty=391515.0, qtyUsdt=78385), CandleApi.PriceQtyInfo(price=0.20001, qty=1787406.0, qtyUsdt=357499), CandleApi.PriceQtyInfo(price=0.2, qty=6120420.0, qtyUsdt=1224084), CandleApi.PriceQtyInfo(price=0.196, qty=461206.0, qtyUsdt=90396), CandleApi.PriceQtyInfo(price=0.195, qty=560324.0, qtyUsdt=109263), CandleApi.PriceQtyInfo(price=0.19, qty=8211853.0, qtyUsdt=1560252), CandleApi.PriceQtyInfo(price=0.18888, qty=1039616.0, qtyUsdt=196362), CandleApi.PriceQtyInfo(price=0.18562, qty=837042.0, qtyUsdt=155371), CandleApi.PriceQtyInfo(price=0.181, qty=1667256.0, qtyUsdt=301773), CandleApi.PriceQtyInfo(price=0.18, qty=2403346.0, qtyUsdt=432602), CandleApi.PriceQtyInfo(price=0.178, qty=520533.0, qtyUsdt=92654), CandleApi.PriceQtyInfo(price=0.176, qty=731768.0, qtyUsdt=128791), CandleApi.PriceQtyInfo(price=0.175, qty=769849.0, qtyUsdt=134723)], asks=[CandleApi.PriceQtyInfo(price=0.22675, qty=319135.0, qtyUsdt=72363), CandleApi.PriceQtyInfo(price=0.22683, qty=441169.0, qtyUsdt=100070), CandleApi.PriceQtyInfo(price=0.227, qty=386798.0, qtyUsdt=87803), CandleApi.PriceQtyInfo(price=0.22722, qty=468413.0, qtyUsdt=106432), CandleApi.PriceQtyInfo(price=0.22724, qty=418885.0, qtyUsdt=95187), CandleApi.PriceQtyInfo(price=0.2273, qty=455877.0, qtyUsdt=103620), CandleApi.PriceQtyInfo(price=0.2275, qty=1407359.0, qtyUsdt=320174), CandleApi.PriceQtyInfo(price=0.22773, qty=1694044.0, qtyUsdt=385784), CandleApi.PriceQtyInfo(price=0.22777, qty=1380001.0, qtyUsdt=314322), CandleApi.PriceQtyInfo(price=0.228, qty=1275239.0, qtyUsdt=290754), CandleApi.PriceQtyInfo(price=0.22833, qty=308255.0, qtyUsdt=70383), CandleApi.PriceQtyInfo(price=0.22861, qty=416111.0, qtyUsdt=95127), CandleApi.PriceQtyInfo(price=0.22863, qty=521696.0, qtyUsdt=119275), CandleApi.PriceQtyInfo(price=0.22886, qty=618978.0, qtyUsdt=141659), CandleApi.PriceQtyInfo(price=0.22887, qty=500173.0, qtyUsdt=114474), CandleApi.PriceQtyInfo(price=0.22888, qty=551235.0, qtyUsdt=126166), CandleApi.PriceQtyInfo(price=0.22889, qty=500046.0, qtyUsdt=114455), CandleApi.PriceQtyInfo(price=0.229, qty=399838.0, qtyUsdt=91562), CandleApi.PriceQtyInfo(price=0.2291, qty=419882.0, qtyUsdt=96194), CandleApi.PriceQtyInfo(price=0.2293, qty=903289.0, qtyUsdt=207124), CandleApi.PriceQtyInfo(price=0.23, qty=2686957.0, qtyUsdt=618000), CandleApi.PriceQtyInfo(price=0.231, qty=405546.0, qtyUsdt=93681), CandleApi.PriceQtyInfo(price=0.232, qty=1010198.0, qtyUsdt=234365), CandleApi.PriceQtyInfo(price=0.23299, qty=1513281.0, qtyUsdt=352579), CandleApi.PriceQtyInfo(price=0.233, qty=435383.0, qtyUsdt=101444), CandleApi.PriceQtyInfo(price=0.235, qty=1439102.0, qtyUsdt=338188), CandleApi.PriceQtyInfo(price=0.236, qty=1152629.0, qtyUsdt=272020), CandleApi.PriceQtyInfo(price=0.2373, qty=1178038.0, qtyUsdt=279548), CandleApi.PriceQtyInfo(price=0.23785, qty=1237466.0, qtyUsdt=294331), CandleApi.PriceQtyInfo(price=0.238, qty=545687.0, qtyUsdt=129873), CandleApi.PriceQtyInfo(price=0.24, qty=4149702.0, qtyUsdt=995928), CandleApi.PriceQtyInfo(price=0.242, qty=2151721.0, qtyUsdt=520716), CandleApi.PriceQtyInfo(price=0.245, qty=1528590.0, qtyUsdt=374504), CandleApi.PriceQtyInfo(price=0.248, qty=566067.0, qtyUsdt=140384), CandleApi.PriceQtyInfo(price=0.24883, qty=756006.0, qtyUsdt=188116), CandleApi.PriceQtyInfo(price=0.249, qty=545577.0, qtyUsdt=135848), CandleApi.PriceQtyInfo(price=0.25, qty=5829383.0, qtyUsdt=1457345), CandleApi.PriceQtyInfo(price=0.25055, qty=326399.0, qtyUsdt=81779), CandleApi.PriceQtyInfo(price=0.252, qty=2237275.0, qtyUsdt=563793), CandleApi.PriceQtyInfo(price=0.253, qty=391947.0, qtyUsdt=99162), CandleApi.PriceQtyInfo(price=0.254, qty=470330.0, qtyUsdt=119463), CandleApi.PriceQtyInfo(price=0.255, qty=2410224.0, qtyUsdt=614607), CandleApi.PriceQtyInfo(price=0.257, qty=1113664.0, qtyUsdt=286211), CandleApi.PriceQtyInfo(price=0.258, qty=869817.0, qtyUsdt=224412), CandleApi.PriceQtyInfo(price=0.2585, qty=344579.0, qtyUsdt=89073), CandleApi.PriceQtyInfo(price=0.2587, qty=1256756.0, qtyUsdt=325122), CandleApi.PriceQtyInfo(price=0.259, qty=601779.0, qtyUsdt=155860), CandleApi.PriceQtyInfo(price=0.2599, qty=1115953.0, qtyUsdt=290036), CandleApi.PriceQtyInfo(price=0.26, qty=6139658.0, qtyUsdt=1596311), CandleApi.PriceQtyInfo(price=0.261, qty=288060.0, qtyUsdt=75183), CandleApi.PriceQtyInfo(price=0.26139, qty=2242773.0, qtyUsdt=586238), CandleApi.PriceQtyInfo(price=0.265, qty=1627340.0, qtyUsdt=431245), CandleApi.PriceQtyInfo(price=0.2664, qty=2001092.0, qtyUsdt=533090), CandleApi.PriceQtyInfo(price=0.2688, qty=310169.0, qtyUsdt=83373), CandleApi.PriceQtyInfo(price=0.26899, qty=335115.0, qtyUsdt=90142), CandleApi.PriceQtyInfo(price=0.27, qty=4633351.0, qtyUsdt=1251004), CandleApi.PriceQtyInfo(price=0.271, qty=491777.0, qtyUsdt=133271), CandleApi.PriceQtyInfo(price=0.275, qty=413974.0, qtyUsdt=113842), CandleApi.PriceQtyInfo(price=0.276, qty=4964650.0, qtyUsdt=1370243), CandleApi.PriceQtyInfo(price=0.277, qty=264132.0, qtyUsdt=73164)])
//        todo по DOGEUSDT видно что есть крупные заявки на покупку, а крупных заявок на продажу дальних на (*Х10) нет, нужно отслеживать

//        2    ZENUSDT    CandleApi.ResponseDto(symbol=ZENUSDT, bids=[], asks=[CandleApi.PriceQtyInfo(price=12.0, qty=7906.2, qtyUsdt=94874), CandleApi.PriceQtyInfo(price=14.0, qty=9383.26, qtyUsdt=131365), CandleApi.PriceQtyInfo(price=14.99, qty=9149.39, qtyUsdt=137149), CandleApi.PriceQtyInfo(price=25.0, qty=8274.8, qtyUsdt=206869), CandleApi.PriceQtyInfo(price=25.16, qty=3303.28, qtyUsdt=83110), CandleApi.PriceQtyInfo(price=28.0, qty=2789.7, qtyUsdt=78111), CandleApi.PriceQtyInfo(price=29.0, qty=2417.29, qtyUsdt=70101), CandleApi.PriceQtyInfo(price=30.0, qty=16329.6, qtyUsdt=489888), CandleApi.PriceQtyInfo(price=40.0, qty=5681.0, qtyUsdt=227240), CandleApi.PriceQtyInfo(price=42.0, qty=4586.56, qtyUsdt=192635), CandleApi.PriceQtyInfo(price=42.19, qty=1702.77, qtyUsdt=71839), CandleApi.PriceQtyInfo(price=42.41, qty=1900.0, qtyUsdt=80579), CandleApi.PriceQtyInfo(price=43.0, qty=2573.39, qtyUsdt=110655), CandleApi.PriceQtyInfo(price=44.0, qty=3545.99, qtyUsdt=156023), CandleApi.PriceQtyInfo(price=45.0, qty=12131.69, qtyUsdt=545926), CandleApi.PriceQtyInfo(price=46.0, qty=3035.85, qtyUsdt=139649), CandleApi.PriceQtyInfo(price=48.0, qty=3684.58, qtyUsdt=176859), CandleApi.PriceQtyInfo(price=49.0, qty=2889.62, qtyUsdt=141591), CandleApi.PriceQtyInfo(price=49.6, qty=1426.15, qtyUsdt=70737), CandleApi.PriceQtyInfo(price=50.0, qty=3547.66, qtyUsdt=177383), CandleApi.PriceQtyInfo(price=52.0, qty=2743.37, qtyUsdt=142655), CandleApi.PriceQtyInfo(price=53.0, qty=11427.66, qtyUsdt=605665), CandleApi.PriceQtyInfo(price=55.0, qty=3045.86, qtyUsdt=167522), CandleApi.PriceQtyInfo(price=56.17, qty=2963.16, qtyUsdt=166440), CandleApi.PriceQtyInfo(price=60.0, qty=2435.12, qtyUsdt=146107), CandleApi.PriceQtyInfo(price=67.0, qty=1593.39, qtyUsdt=106757), CandleApi.PriceQtyInfo(price=68.0, qty=1105.27, qtyUsdt=75158), CandleApi.PriceQtyInfo(price=68.58, qty=2000.0, qtyUsdt=137160), CandleApi.PriceQtyInfo(price=68.88, qty=1120.15, qtyUsdt=77155), CandleApi.PriceQtyInfo(price=70.0, qty=1887.73, qtyUsdt=132141), CandleApi.PriceQtyInfo(price=71.8, qty=2017.68, qtyUsdt=144869), CandleApi.PriceQtyInfo(price=75.0, qty=1694.5, qtyUsdt=127087), CandleApi.PriceQtyInfo(price=78.0, qty=169000.51, qtyUsdt=13182039), CandleApi.PriceQtyInfo(price=79.0, qty=1281.07, qtyUsdt=101204), CandleApi.PriceQtyInfo(price=80.0, qty=2429.78, qtyUsdt=194382), CandleApi.PriceQtyInfo(price=82.0, qty=1475.08, qtyUsdt=120956), CandleApi.PriceQtyInfo(price=90.0, qty=2018.4, qtyUsdt=181656), CandleApi.PriceQtyInfo(price=99.0, qty=1492.78, qtyUsdt=147785), CandleApi.PriceQtyInfo(price=100.0, qty=3000.24, qtyUsdt=300024), CandleApi.PriceQtyInfo(price=120.0, qty=918.26, qtyUsdt=110191), CandleApi.PriceQtyInfo(price=128.61, qty=2004.18, qtyUsdt=257757), CandleApi.PriceQtyInfo(price=150.0, qty=598.1715, qtyUsdt=89725), CandleApi.PriceQtyInfo(price=160.0, qty=663.8409, qtyUsdt=106214), CandleApi.PriceQtyInfo(price=182.0, qty=9485.2042, qtyUsdt=1726307), CandleApi.PriceQtyInfo(price=200.0, qty=702.5873, qtyUsdt=140517), CandleApi.PriceQtyInfo(price=519.0, qty=357.82, qtyUsdt=185708), CandleApi.PriceQtyInfo(price=690.0, qty=121.8284, qtyUsdt=84061)])
//          todo ZENUSDT стоит 10 рост до 690 щас лежит на дне дно купить на споте и на

//  todo нужно сделать по всем валютам проверить средний обьем за 10 дней и подумать когда он изменятся начнет
//        и посмотреть обьем в пампах когда вылетает 200-500 т. за 5 минут


//        https://www.binance.com/ru/trade/FTT_USDT
        System.out.println("========================");
    }

    //    https://api.binance.com/api/v3/exchangeInfo

    public static BookInfoDto getInfoBookBySymbol(String symbol) {
        String url = "https://api.binance.com/api/v3/depth?symbol=" + symbol + "&limit=5000";
        String response;
        response = getResponse(url, 3);

        if (isNull(response)) {
            return BookInfoDto.builder().build();
        }
        JSONObject jsonObject = new JSONObject(response);
        List<BookInfoDto.PriceQtyInfo> bids = getPriceQtyInfoList(new JSONArray(new JSONArray(jsonObject.get("bids").toString()).toString()));
        List<BookInfoDto.PriceQtyInfo> asks = getPriceQtyInfoList(new JSONArray(new JSONArray(jsonObject.get("asks").toString()).toString()));
        return BookInfoDto.builder()
                .asks(asks)
                .bids(bids)
                .symbol(symbol)
                .build();
    }

    private static List<BookInfoDto.PriceQtyInfo> getPriceQtyInfoList(JSONArray jsonArray) {
        List<BookInfoDto.PriceQtyInfo> priceQtyInfos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray info = jsonArray.getJSONArray(i);
            double price = Double.parseDouble(info.get(0).toString());
            double qty = Double.parseDouble(info.get(1).toString());
            int qtyUsdt = (int) (price * (qty * 2700));

            if (qtyUsdt > 70000) {
                priceQtyInfos.add(BookInfoDto.PriceQtyInfo.builder()
                        .price(price)
                        .qty(qty)
                        .qtyUsdt(qtyUsdt)
                        .build());
            }
        }
        return priceQtyInfos;
    }
}
