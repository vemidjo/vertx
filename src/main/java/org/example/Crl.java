package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

/**
 * @author vemidjo
 */
public class Crl {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        HttpServer server = vertx.createHttpServer(TlsHelper.createHttpServerOptions());
        server.requestHandler(System.out::println)
                .listen(8080);

        vertx.setPeriodic(100, p -> server.updateSSLOptions(TlsHelper.createSslOptions())
                .onSuccess(result -> System.out.println("success"))
                .onFailure(throwable -> throwable.printStackTrace()));
    }

    private class TlsHelper {
        private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDMSdoXjp1FoJb6\n" +
                "mKi6SEaWw0/xo8Te3aC4NHMfH23/fa8K2woJkSlDSNaNl7wcxlv0w+a1P4ol9kj+\n" +
                "I6pY4+h1sBDF/tNaGMknj/sNhBbj7sebC9ykIXb/+EyGZk8+l2xQEuX8sdJmn448\n" +
                "ujB0Tyso6C/KRxgrgeRHtDSuMyK09TWb1Yt7vJOoEvRpqzxWauWHfJuyUb3P0/Uc\n" +
                "UBaRKMte11bJMDf5ISTzhka6nPzMlcwWPJgmqWNLA1e6skTlob+xidH2duhY2NK9\n" +
                "ou/M8qPmGzFwb0z+ZB025s9MhHfRCIUmxd9a0f7yihKWAsougJ5A5OyPHY+l1E1E\n" +
                "l/O5D0rVAgMBAAECggEAQ0SeN9uSFfdHP8U86foapFIz5NGUZf58iatbrQfgZJyy\n" +
                "ItT1fSj7eVhmrcElBCSc6wAG2+/MQavTZTvBP/vr87TCETBTz/sRYaGbfVvdXbim\n" +
                "qXtCcBlE4gyUxjyyI14hIEzYxliXe4tWftCgW+wlvhaWhfjbEJh43tOL9UCJUSRG\n" +
                "iBk6r2bwQWeCvhaEGZzSp20vXr5SmsU56h3lmMiqePtIXhiU9iQ1g8np1CvrpcwE\n" +
                "eL1cb+1E+OHQst2sfQzOKah+ZQKWHZdyk5Ju+MhI4T4eyd133gtltFQSRi/yr3LM\n" +
                "mXd6rzFrDKHj2BzZzXtsdg8H2EwrbTk2pge0DVl4AQKBgQDteo2X11+cqLfMd89Q\n" +
                "Q0Gu2RkSHmv0nJJxSkspxmnOT2tilb3kNe2eMDlyt6HeTvByAjQTCHsde1DxMQHp\n" +
                "ekqM1UguxeCMI9vjrCcgfYtNtWFIxWa+MYqBMXEWrhwDsRdbVD+FT5oyiMywZRNG\n" +
                "tFXLdBWlot9Qo1hj1G/zfpGggQKBgQDcOKFFA3vcFqhPZ8K/O1dhbpGUWZt2DXBv\n" +
                "qU7nrtFcavtiIR596hj1LqRPK6xmQ6oR8XeKy/mLKN9RdhxdMKzntzZ8W1RRexpB\n" +
                "vXuW/qZ5hgIU/OJKnk8aGlE/Zrii0vVWqSV0TNeKYkddzUePgI8Egu8YJM4TDaCk\n" +
                "Bpi4eDUAVQKBgQDh0H0/e27RWqWbafnaSnn0tz9duUvSR+gAjjK2wJLvuRlVnyn4\n" +
                "DUer+hNGgFMNP2boIiw+RPw85nvrPTyiNMUa8dw3jbWdzKNU9OgnvvltpSGivwKZ\n" +
                "rCYV/1uXw2ukHwskrxHPxAfOMcLhoNsZinVihznmTN+ooAp2sxTZJut3AQKBgQDH\n" +
                "LCynZ2w5dQCKuJSJMlloVIE0RAr3rVX4l482uYS+6j8r1DpotTYXYjTXKe/FmgMS\n" +
                "MWrlzG+IrdG0MneazTBEbg7IUC2leAoaVQ6XVP8pv8rYQpND24Q9XnoHNpddJCEi\n" +
                "XN70HZ9/mD1lmAjVv/cZpHYsRvVehB+62gB8LrP49QKBgQC5ToU51ryckbTsB0V6\n" +
                "LNjRkIa3JmQ/Wq6uZdQ2QbRBqdKKSVxAwcqu64B47vZ4xkjsigmnvcy/CO4Z8b25\n" +
                "mPPLvPR9KwpGxhBQ2D2DyePoKfYZovBnBfe+K9zEdI1hSm4sbJU3N4p/16PFZ3ei\n" +
                "J8Bu2f3tj9R4Y2tmAreAvuZu5g==\n" +
                "-----END PRIVATE KEY-----";

        private static final String PEM_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n" +
                "MIIEiDCCAnCgAwIBAgICEAUwDQYJKoZIhvcNAQELBQAwYjELMAkGA1UEBhMCUlMx\n" +
                "DzANBgNVBAgMBlNlcmJpYTEQMA4GA1UECgwHVmVyaXNlYzEQMA4GA1UECwwHVmVy\n" +
                "aXNlYzEeMBwGA1UEAwwVRXZlbnRCdXMgSW50ZXJtZWRpYXRlMCAXDTIyMDgyMzEy\n" +
                "NTE1M1oYDzIxMjIwNzMwMTI1MTUzWjBtMQswCQYDVQQGEwJSUzEPMA0GA1UECAwG\n" +
                "U2VyYmlhMREwDwYDVQQHDAhCZWxncmFkZTEQMA4GA1UECgwHVmVyaXNlYzEQMA4G\n" +
                "A1UECwwHVmVyaXNlYzEWMBQGA1UEAwwNQ3J5cHRvU2VydmljZTCCASIwDQYJKoZI\n" +
                "hvcNAQEBBQADggEPADCCAQoCggEBAMxJ2heOnUWglvqYqLpIRpbDT/GjxN7doLg0\n" +
                "cx8fbf99rwrbCgmRKUNI1o2XvBzGW/TD5rU/iiX2SP4jqljj6HWwEMX+01oYySeP\n" +
                "+w2EFuPux5sL3KQhdv/4TIZmTz6XbFAS5fyx0mafjjy6MHRPKyjoL8pHGCuB5Ee0\n" +
                "NK4zIrT1NZvVi3u8k6gS9GmrPFZq5Yd8m7JRvc/T9RxQFpEoy17XVskwN/khJPOG\n" +
                "Rrqc/MyVzBY8mCapY0sDV7qyROWhv7GJ0fZ26FjY0r2i78zyo+YbMXBvTP5kHTbm\n" +
                "z0yEd9EIhSbF31rR/vKKEpYCyi6AnkDk7I8dj6XUTUSX87kPStUCAwEAAaM7MDkw\n" +
                "CQYDVR0TBAIwADAfBgNVHSMEGDAWgBSIHeDtmKggai69/kWKQK1WDM258zALBgNV\n" +
                "HQ8EBAMCA4gwDQYJKoZIhvcNAQELBQADggIBALPdU2g85KvWjiSt0yOpUXoXQ8lX\n" +
                "3oSePSqKqKnlircQfMQ3AHWyf8+wvrAf871Eau7gx45tKQKY0x1xJ99or/Ttm8Fh\n" +
                "hOtP41RoOVnRXDWiuVOHNqiyERwS65Bfz7aFKZdNBir1NpNfBDjZjtGdLb5Ss4aE\n" +
                "2kaQuD7yvhhMl6Q9ViZ6RzQXvHvqeccVhJOCJqeWty4jdfe/z8b+5c0MDs36qiTy\n" +
                "vpveM5s7EmUizt4fhVmnf+Au6GDdGmDA23XrLyFtgyBnzSd2PJMSM74NN4S37N3D\n" +
                "p6HoszkIAAbNzE1s7L3FOY9ISDskCF6jDWn8dP6JyXZHROuZIWs7H8VSKr1cisSC\n" +
                "nOcipHi0GbRtZn2jou/+1POxA5Ceql7LbErCm3jZD8k7xmVeZIu1GlhVGo0YH/pT\n" +
                "zhPrNtz0fbxovHCwXnLjT3oNX5NR7m/KckxuB4Y4rBSsSRHDIZv1KitcXIZJibmU\n" +
                "bnoL0xkUzZJnSg5nwFoipjx7vD+5nbtTeL/bNJvlm1X4QGt1MhNY9Qq3wimCL/1y\n" +
                "xjE5VLWjs5nK13y4VP4dMUnBFQSn7NYIoC9Pl5209k7U5clT92MtZB1E/+R/stGs\n" +
                "d04XpY68HBQD6kqB1INTSsEl/Dgz7eb73LJQ2P3Q1oHEkKVp4C4I7T0yyszmCxUY\n" +
                "SqNRK4fUDlPmSeyM\n" +
                "-----END CERTIFICATE-----";

        private static final String TRUST_CHAIN = "-----BEGIN CERTIFICATE-----\n" +
                "MIIFmjCCA4KgAwIBAgICEAIwDQYJKoZIhvcNAQELBQAwZzELMAkGA1UEBhMCUlMx\n" +
                "DzANBgNVBAgMBlNlcmJpYTERMA8GA1UEBwwIQmVsZ3JhZGUxEDAOBgNVBAoMB1Zl\n" +
                "cmlzZWMxEDAOBgNVBAsMB1ZlcmlzZWMxEDAOBgNVBAMMB1Jvb3QgQ0EwIBcNMjIw\n" +
                "ODIzMTI0MzU3WhgPMjEyMjA3MzAxMjQzNTdaMGIxCzAJBgNVBAYTAlJTMQ8wDQYD\n" +
                "VQQIDAZTZXJiaWExEDAOBgNVBAoMB1ZlcmlzZWMxEDAOBgNVBAsMB1ZlcmlzZWMx\n" +
                "HjAcBgNVBAMMFUN1c3RvbWVyIEludGVybWVkaWF0ZTCCAiIwDQYJKoZIhvcNAQEB\n" +
                "BQADggIPADCCAgoCggIBAN8b1hPhfm7qEgSINVF/+HctOBi/4CQCC/7rKGyey8cX\n" +
                "5/7CgTBVNbIGgy+IGNHCB60x934cYJzS/Oq3XnkV6dnYoYGVGBxv+/kx6gQnLCVq\n" +
                "r/4wVFOt3YVANsA64U3Elb1deBdg5hCB3E6wj0AiET9/QKmvdF4wkbwPmLvpFskW\n" +
                "61bP6XDeem0fcIXYq7vG6+xDx3ZK3w6kxtzS1jWisE/k4T9w/+p5271F9Qp/+EqC\n" +
                "h1DOa3ZEJMxocmZdYIpxKLQCOiazYo4z9Mb+zxHZEPrHMq6NmWJo4w68MCvJnvhT\n" +
                "1UsA0L/K04waMfAHMhamyT9xUdDKrewzDG5qqA119W6Sg3yVAfAPH8oBjCbiGoR9\n" +
                "YkaGQS+V+KLLvR5isBQMXD3B29/WXa93nTV19cGV0g1xpHvk0m9iclgvnrC5D0Xw\n" +
                "q108Xh1Q0bzj1QhH0qMY94G68rO+486pRR8APPfRZlNkY8BXX4xloWqdVMe2IpTY\n" +
                "j6FEMOQhFP/f/5D9Jec1kGHbClWrZLgJGny6kSsN0eDGVhw8fg4/ZHgsdAQK/2gQ\n" +
                "FwuElwg9l84xHitgeaATN9eYnbed7KhH3la/OEDuBx4DEomZ1tSUJCU+xPlnkung\n" +
                "x2WJ0gi2afeUvQ2wryNlmKqatrcRT9XlpMO7XAvnDIFi2/4I/gEddM7AiZPhWR+V\n" +
                "AgMBAAGjUzBRMB0GA1UdDgQWBBTde3wA3i8QEhDaAb9FNZrtMTBi7zAfBgNVHSME\n" +
                "GDAWgBT+LllmjFCVUDLb0J943aI/LsoR4zAPBgNVHRMECDAGAQH/AgEAMA0GCSqG\n" +
                "SIb3DQEBCwUAA4ICAQDaM2SaWhxb1SQTkLSDKFhcCtGC2LUefL5EdvgldQRfZFCB\n" +
                "+bUcWCKHuCMjttInFU940jg6jvpB8mEPFIpJfJVM50VX/P1O2ScbeModSb9FyKfD\n" +
                "E6IoMRLjlm/Jb8ohnvo57fF032gnldxysOq9SMDVhhVZ0hc0VIpH4xDQm3GGS642\n" +
                "nKY1lpCJEEWEvDnjhVgPSbRnGAgNEOGvp/nylOK+3pLVWn2Y/uhceTJCkZYi+zKD\n" +
                "CjQ5OzqnRC8oTd/lt+89BjKEL+rPkSIEocvKd+lMq/z+8+I3wxINuPGcffmZubdU\n" +
                "tIPhm3WHxHEKit4CAwczxH8b02VmsxPuUWiEkWYdZZmux1URdv2uM49d5mYpPaA6\n" +
                "2zSFJpSFA8C+I0FtZTp8XAWhbdsHtlOyx8L7kFyWBpo0jsDLUvEGa5wr+PvftP9r\n" +
                "q9fkdRncLYg6PPqYDmuQmuum+VLs8OBUAyDPXOnLBvlevu3sobbZ9J2dVEFQqhWB\n" +
                "W2Ysu3uGBwXs7rmYcMOb4drlqSTQ+/YEFhfL3BHfd/KymC4uM00vOeqoaiX3suwa\n" +
                "IoIIuM4BQI5xDGMh4f4tSWvHYpd3nAcPszcrsJ+6ByN8jGvZRqz2+S2lsA28O23M\n" +
                "zDuwgPZC9XSrgsz1WMR0YRg7hfY8pHS++t0NuxUw9MzB4OgDv+ihZ0jqXJ533Q==\n" +
                "-----END CERTIFICATE-----";

        private static byte[] CRL;

        static {
            try {
                CRL = Objects.requireNonNull(Crl.class.getClassLoader().getResourceAsStream("crl")).readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static HttpServerOptions createHttpServerOptions() {
            HttpServerOptions httpServerOptions = new HttpServerOptions();

            httpServerOptions.setSsl(true);
            httpServerOptions.setKeyCertOptions(createKeyCertOptions());
            httpServerOptions.setTrustOptions(createTrustOptions());
            httpServerOptions.addCrlValue(Buffer.buffer(Base64.getDecoder().decode(CRL)));

            return httpServerOptions;
        }

        public static SSLOptions createSslOptions() {
            return new SSLOptions()
                    .setKeyCertOptions(createKeyCertOptions())
                    .setTrustOptions(createTrustOptions())
                    .addCrlValue(Buffer.buffer(Base64.getDecoder().decode(CRL)));
        }

        private static KeyCertOptions createKeyCertOptions() {
            return new PemKeyCertOptions()
                    .setKeyValue(Buffer.buffer(PRIVATE_KEY))
                    .setCertValue(Buffer.buffer(PEM_CERTIFICATE));
        }

        private static TrustOptions createTrustOptions() {
            return new PemTrustOptions()
                    .addCertValue(Buffer.buffer(TRUST_CHAIN));
        }
    }
}
