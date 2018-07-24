package fr.sayasoft.fake.zinc;

import com.google.gson.Gson;
import fr.sayasoft.zinc.sdk.domain.OrderRequest;
import fr.sayasoft.zinc.sdk.domain.OrderResponse;
import fr.sayasoft.zinc.sdk.domain.ZincConstants;
import fr.sayasoft.zinc.sdk.domain.ZincError;
import fr.sayasoft.zinc.sdk.enums.ZincErrorCode;
import fr.sayasoft.zinc.sdk.enums.ZincWebhookType;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@Log4j
public class FakeZincController {

    private static final String template = "Hello, %s!";
    public static final String GET_ORDER_RESPONSE = "{\n" +
            "  \"_type\" : \"order_response\",\n" +
            "  \"price_components\" : {\n" +
            "    \"shipping\" : 0,\n" +
            "    \"subtotal\" : 1999,\n" +
            "    \"tax\" : 0,\n" +
            "    \"total\" : 1999\n" +
            "  },\n" +
            "  \"merchant_order_ids\" : [\n" +
            "    {\n" +
            "      \"merchant_order_id\" : \"112-1234567-7272727\",\n" +
            "      \"merchant\" : \"amazon\",\n" +
            "      \"account\" : \"timbeaver@gmail.com\",\n" +
            "      \"placed_at\" : \"2014-07-02T23:51:08.366Z\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"tracking\" : [\n" +
            "    {\n" +
            "      \"merchant_order_id\" : \"112-1234567-7272727\",\n" +
            "      \"carrier\" : \"Fedex\",\n" +
            "      \"tracking_number\" : \"9261290100129790891234\",\n" +
            "      \"obtained_at\" : \"2014-07-03T23:22:48.165Z\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String GET_PRODUCT_OFFER_RESPONSE = "{\n" +
            "  \"retailer\": \"amazon\",\n" +
            "  \"status\": \"completed\",\n" +
            "  \"offers\":[\n" +
            "    {\n" +
            "      \"addon\": false,\n" +
            "      \"condition\": \"New\",\n" +
            "      \"handling_days_max\": 0,\n" +
            "      \"handling_days_min\": 0,\n" +
            "      \"international\": false,\n" +
            "      \"merchant_id\": \"ATVPDKIKX0DER\",\n" +
            "      \"offerlisting_id\": \"lUai8vEbhC%2F2vYZDwaePlc4baWiHzAy9XJncUR%2FpQ9l4VOrs%2FfpYt4ZtreQaB%2BPL1xJwz5OpIc%2BJjyymHg3iv4YkZvWy5z7flil7n7lUDWNPY76YUhMNdw%3D%3D\",\n" +
            "      \"price\": 9.79,\n" +
            "      \"ship_price\": 0,\n" +
            "      \"prime\": true,\n" +
            "      \"prime_only\": false,\n" +
            "      \"seller_name\": \"Amazon.com\",\n" +
            "      \"seller_num_ratings\": 1000000,\n" +
            "      \"seller_percent_positive\": 100\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String GET_PRODUCT_DETAILS_RESPONSE = "{\n" +
            "    \"status\": \"completed\",\n" +
            "    \"original_retail_price\": 899,\n" +
            "    \"timestamp\": 1515775557,\n" +
            "    \"all_variants\": [\n" +
            "        {\n" +
            "            \"variant_specifics\": [\n" +
            "                {\n" +
            "                    \"dimension\": \"Size\",\n" +
            "                    \"value\": \"2\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"product_id\": \"B00Q3H18EQ\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"variant_specifics\": [\n" +
            "                {\n" +
            "                    \"dimension\": \"Size\",\n" +
            "                    \"value\": \"1\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"product_id\": \"B00KFP6NHO\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"retailer\": \"amazon\",\n" +
            "    \"feature_bullets\": [\n" +
            "        \"Includes four freeze-and-feed popsicle molds with handles shaped perfectly for little hands\",\n" +
            "        \"Perfect for fresh homemade puree popsicles - turn fresh fruit/veggie puree or juice into 1 fl. oz popsicles\",\n" +
            "        \"Wide popsicle-holder base catches drips as the popsicle melts to reduce the risk of messes\",\n" +
            "        \"Great for teething babies to help soothe sore gums\",\n" +
            "        \"6 Months + / BPA Free\"\n" +
            "    ],\n" +
            "    \"variant_specifics\": [\n" +
            "        {\n" +
            "            \"dimension\": \"Size\",\n" +
            "            \"value\": \"1\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"main_image\": \"https://images-na.ssl-images-amazon.com/images/I/61K0YbuLi-L.jpg\",\n" +
            "    \"images\": [\n" +
            "        \"https://images-na.ssl-images-amazon.com/images/I/61K0YbuLi-L.jpg\",\n" +
            "        \"https://images-na.ssl-images-amazon.com/images/I/81KtOn8ddTL.jpg\",\n" +
            "        \"https://images-na.ssl-images-amazon.com/images/I/71%2BruDKMSoL.jpg\",\n" +
            "        \"https://images-na.ssl-images-amazon.com/images/I/91AE6dpp5EL.jpg\",\n" +
            "        \"https://images-na.ssl-images-amazon.com/images/I/61FQEQJR2HL.jpg\",\n" +
            "        \"https://images-na.ssl-images-amazon.com/images/I/511agWyBf3L.jpg\",\n" +
            "        \"https://images-na.ssl-images-amazon.com/images/I/31cC6K6y%2ByL.jpg\",\n" +
            "        \"https://images-na.ssl-images-amazon.com/images/I/31ocdUye0ML.jpg\"\n" +
            "    ],\n" +
            "    \"package_dimensions\": {\n" +
            "        \"weight\": {\n" +
            "            \"amount\": 8.5,\n" +
            "            \"unit\": \"ounces\"\n" +
            "        },\n" +
            "        \"size\": {\n" +
            "            \"width\": {\n" +
            "                \"amount\": 4,\n" +
            "                \"unit\": \"inches\"\n" +
            "            },\n" +
            "            \"depth\": {\n" +
            "                \"amount\": 5.8,\n" +
            "                \"unit\": \"inches\"\n" +
            "            },\n" +
            "            \"length\": {\n" +
            "                \"amount\": 5.8,\n" +
            "                \"unit\": \"inches\"\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    \"epids\": [\n" +
            "        {\n" +
            "            \"type\": \"MPN\",\n" +
            "            \"value\": \"5438\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"type\": \"UPC\",\n" +
            "            \"value\": \"048526054381\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"type\": \"EAN\",\n" +
            "            \"value\": \"0048526054381\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"product_id\": \"B00KFP6NHO\",\n" +
            "    \"asin\": \"B00KFP6NHO\",\n" +
            "    \"ship_price\": 0,\n" +
            "    \"categories\": [\n" +
            "        \"Home & Kitchen\",\n" +
            "        \"Kitchen & Dining\",\n" +
            "        \"Kitchen Utensils & Gadgets\",\n" +
            "        \"Specialty Tools & Gadgets\",\n" +
            "        \"Ice Pop Molds\"\n" +
            "    ],\n" +
            "    \"review_count\": 829,\n" +
            "    \"epids_map\": {\n" +
            "        \"MPN\": \"5438\",\n" +
            "        \"UPC\": \"048526054381\",\n" +
            "        \"EAN\": \"0048526054381\"\n" +
            "    },\n" +
            "    \"title\": \"Nuby Garden Fresh Fruitsicle Frozen Pop Tray\",\n" +
            "    \"brand\": \"Nuby\",\n" +
            "    \"product_description\": \"Size:1  Nuby's Garden Fresh Fruitsicle Frozen Popsicle Tray\\nis specially designed for making fresh puree popsicles at home. Nuby’s\\nFruitsicles are the perfect size for baby’s small hands and are designed to\\ncatch drips as the pop melts. Fruitsicles are perfect for teething babies with\\nsore gums. This set includes four fruitsicle handles and a tray to mold the\\npops while keeping them in place while in your freezer. To use: fill\\ncompartments with fresh puree, breast milk, or juice. Snap handles into mold\\nand freeze until solid. BPA Free. By Nuby\",\n" +
            "    \"product_details\": [\n" +
            "        \"Product Dimensions: 5.8 x 5.8 x 4 inches ; 7.8 ounces\",\n" +
            "        \"Shipping Weight: 8.5 ounces\",\n" +
            "        \"Domestic Shipping: Item can be shipped within U.S.\",\n" +
            "        \"UPC: 048526054381 013513034066\",\n" +
            "        \"Item model number: 5438\"\n" +
            "    ],\n" +
            "    \"question_count\": 26,\n" +
            "    \"stars\": 4.5,\n" +
            "    \"price\": 799\n" +
            "}";

    public static final String POST_ORDER_RESPONSE_TO_BE_REPLACED = "XXX";
    public static final String POST_ORDER_RESPONSE = "{\n" +
            "  \"request_id\": \"fakeRequestIdStart-" + POST_ORDER_RESPONSE_TO_BE_REPLACED + "-fakeRequestIdEnd\"\n" +
            "}";

    private final AtomicLong counter = new AtomicLong();

    @SuppressWarnings("unused")
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @SuppressWarnings("unused")
    @RequestMapping(
            value = "/v1/orders/{request_id}",
            method = RequestMethod.GET,
            produces = "application/json; charset=UTF-8"
    )
    public String getOrder(@PathVariable(value = "request_id") String requestId) {
        log.info("Received request to path: GET " + "/v1/orders/" + requestId);
        log.info("Will return: " + GET_ORDER_RESPONSE);
        return GET_ORDER_RESPONSE;
    }

    /**
     * Fake method to test order posting.<br/>
     * Conventions for testing:
     * <ul>
     * <li>if the unmarshalled OrderRequest has a field clientNotes that is a ZincErrorCode, then a ZincError will be returned.</li>
     * <li>else a response containing the idemPotency in the requestId is returned</li>
     * <li>when webhooks are given in the OrderRequest in parameter, then these webhooks are called in the order they are written, with a pause
     * (<code>Thread.sleep</code>) of 30 seconds</li>
     * </ul>
     */
    @SuppressWarnings("unused")
    @RequestMapping(
            value = "/v1/orders",
            method = RequestMethod.POST,
            produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<?> postOrder(@RequestBody String json) {
        log.info("Received request to path: POST " + "/v1/orders/" + json);
        final Gson gson = new Gson();
        final OrderRequest orderRequest = gson.fromJson(json, OrderRequest.class);
        // can be null
        final String idempotencyKey = orderRequest.getIdempotencyKey();

        try {
            if (null != orderRequest.getClientNotes()) {
                final ZincErrorCode zincErrorCode = ZincErrorCode.valueOf(orderRequest.getClientNotes().toString());
                final Map<String, String>  data = new HashMap<>(1);
                data.put("fakeField", idempotencyKey);
                final ZincError zincError = ZincError.builder()
                        .type(ZincConstants.error)
                        .code(zincErrorCode)
                        .data(data)
                        .message(zincErrorCode.getMeaning())
                        .orderRequest(orderRequest)
                        .build();
                log.info("Received request to generate error code, returning: " + zincError);
            /*
            Precision obtained from Zinc support: although an error message is returned, the HTTP header is a 200
            (and not 4XX as may be assumed)
            */
                return new ResponseEntity<>(
                        zincError,
                        HttpStatus.OK);
            }
        } catch (IllegalArgumentException e) {
            // nothing to do
            if (log.isInfoEnabled()) {
                log.info("received clientNotes: " + orderRequest.getClientNotes() + " ; will go on");
            }
        } catch (Exception e) {
            log.error("An error occured", e);
        }

        if (!orderRequest.getWebhooks().isEmpty()) {
            scheduleCallbackWebHooks(orderRequest);
        }

        return new ResponseEntity<>(
                POST_ORDER_RESPONSE.replace(POST_ORDER_RESPONSE_TO_BE_REPLACED, idempotencyKey),
                HttpStatus.CREATED);
        // TODO call the webhooks if present
    }

    protected void scheduleCallbackWebHooks(final OrderRequest orderRequest) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                callbackWebHooks(orderRequest);
            }
        }, 10000); // TODO parametrize
    }


    protected void callbackWebHooks(OrderRequest orderRequest) {
        final RestTemplate restTemplate = new RestTemplate();
        final OrderResponse orderResponse = OrderResponse.builder().orderRequest(orderRequest).build();
        orderRequest.getWebhooks()
                .forEach((zincWebhookType, url) -> {
                            log.info("Calling webhook for zincWebhookType: " + zincWebhookType + " and URL: " + url);
                            try {
                                Thread.sleep(30000); // TODO parametrize
                            } catch (InterruptedException e) {
                                log.error("InterruptedException", e);
                            }
                            try {
                                restTemplate.postForObject(url, orderResponse, String.class);
                            } catch (RestClientException e) {
                                log.error("RestClientException", e);
                            }
                        }
                );
    }

    /* eg http://localhost:9090/webhook/statusUpdated/12345 */
    @RequestMapping(value = "/webhook/{webhookType}/{requestId}", method = RequestMethod.POST)
    @ResponseBody
    public String webhook(@PathVariable("webhookType") ZincWebhookType zincWebhookType,
                          @PathVariable("requestId") String requestId,
                          @RequestBody String json) {
        log.info("hook with request parameters: " + zincWebhookType + ", " + requestId);
        log.info("and request body            : " + json);
        return "OK";
    }

    @SuppressWarnings("unused")
    @RequestMapping(
            value = "/v1/products/{product_id}/offers",
            method = RequestMethod.GET,
            produces = "application/json; charset=UTF-8"
    )
    /** eg https://api.zinc.io/v1/products/0923568964/offers?retailer=amazon  */
    public String getProductOffer(
            @PathVariable(value = "product_id", required = true) String productId,
            @RequestParam(value = "retailer", required = true) String retailer,
            @RequestParam(value = "max_age", required = false) Long maxAge,
            @RequestParam(value = "newer_than", required = false) Long newerThan, // timestamp in seconds, ex: 1522268852 for 28/March/2018 at 22:27:32
            @RequestParam(value = "async", required = false) Boolean async
    ) {
        log.info("Received request to path: GET " + "/v1/products/" + productId + "/offers" + " (product price)");
        log.info("Received parameters: "
                + "retailer: " + retailer
                + ", max_age: " + maxAge
                + ", newer_than: " + newerThan
                + ", async: " + async
        );
        log.info("Will return: " + GET_PRODUCT_OFFER_RESPONSE);
        return GET_PRODUCT_OFFER_RESPONSE;
    }

    @SuppressWarnings("unused")
    @RequestMapping(
            value = "/v1/products/{product_id}",
            method = RequestMethod.GET,
            produces = "application/json; charset=UTF-8"
    )
    /** eg https://api.zinc.io/v1/products/0923568964?retailer=amazon   */
    public String getProductDetails(
            @PathVariable(value = "product_id", required = true) String productId,
            @RequestParam(value = "retailer", required = true) String retailer,
            @RequestParam(value = "max_age", required = false) Long maxAge,
            @RequestParam(value = "newer_than", required = false) Long newerThan, // timestamp in seconds, ex: 1522268852 for 28/March/2018 at 22:27:32
            @RequestParam(value = "async", required = false) Boolean async
    ) {
        log.info("Received request to path: GET " + "/v1/products/" + productId + " (product details)");
        log.info("Received parameters: "
                + "retailer: " + retailer
                + ", max_age: " + maxAge
                + ", newer_than: " + newerThan
                + ", async: " + async
        );
        log.info("Will return: " + GET_PRODUCT_DETAILS_RESPONSE);
        return GET_PRODUCT_DETAILS_RESPONSE;
    }

}