//package com.messenger.jwt;
//
//import io.jsonwebtoken.security.Keys;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//public class JwtKeys {
//
//    // kid-key list
//    private static final Map<String, String> SECRET_KEY_MAP = Map.of(
//            "key1", "secret1",
//            "key2", "secret2"
//    );
//
//    private static final String[] KID_SET = SECRET_KEY_MAP.keySet().toArray(new String[0]);
//    private static final Random RANDOM_INDEX = new Random();
//
//    /**
//     * SECRET_KEY_MAP 에서 랜덤한 key 가져오기
//     * @return Pair (kid, key)
//     */
//    public static List<Object> getRandomKey() {
//        String kid = KID_SET[RANDOM_INDEX.nextInt(KID_SET.length)];
//        String secretKey = SECRET_KEY_MAP.get(kid);
//        return Arrays.asList(kid, Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)));
//    }
//
//    /**
//     * kid로 key 찾기
//     * @param kid
//     * @return key
//     */
//    public static Key getKey(String kid) {
//        String key = SECRET_KEY_MAP.getOrDefault(kid, null);
//        if (key == null) {
//            return null;
//        }
//        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
//    }
//
//}
