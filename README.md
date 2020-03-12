# Symphony Bot SDK

[![CircleCI](https://circleci.com/gh/SymphonyPlatformSolutions/symphony-bot-sdk-java.svg?style=svg&circle-token=f7891ef2a6f117938ef4ee885076c2d2bcd9b9cd)](https://circleci.com/gh/SymphonyPlatformSolutions/symphony-bot-sdk-java) [![Java](https://img.shields.io/badge/JDK-1.8-blue.svg)](https://www.oracle.com/technetwork/java/javase/documentation/index.html) [![Maven](https://img.shields.io/badge/MAVEN-3.0.5+-blue.svg)](https://maven.apache.org/guides/index.html)

This SDK is managing all bot interactions from handling bot commands to receiving notifications from external
systems and push them as symphony messages.


## Summary

* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Setting the service account](#setting-the-service-account)
  * [POD configuration](#pod-configuration)
  * [Running locally](#running-locally)
  * [Verify your setup](#verify-your-setup)
* [Testing commands](#testing-commands)
  * [Help command](#help-command)
  * [Hello command](#hello-command)
  * [Create notification command](#create-notification-command)
  * [Login command](#login-command)
  * [Quote command](#quote-command)
  * [Template command](#template-command)
  * [Register quote command](#register-quote-command)
  * [Attachment command](#attachment-command)
  * [Broadcast command](#broadcast-command)
  * [Default response](#default-response)
* [Testing events](#testing-events)
* [Testing notifications](#testing-notifications)
* [Adding bot commands](#adding-bot-commands)
  * [Command initialization](#command-initialization)
  * [Default responses](#default-responses)
  * [Multi response command handler](#multi-response-command-handler)
  * [Authenticating to external system](#authenticating-to-external-system)
    * [AuthenticationProvider](#authenticationprovider)
    * [AuthenticatedCommandHandler](#authenticatedcommandhandler)
  * [Command Matcher](#command-matcher)
* [Handling Symphony events](#handling-symphony-events)
  * [Available Symphony events](#available-symphony-events)
  * [Permission for bots in public rooms](#permission-for-bots-in-public-rooms)
* [Working with Symphony Elements](#working-with-symphony-elements)
  * [ElementsActionHandler](#elementsactionhandler)
* [Receiving notifications](#receiving-notifications)
  * [Processing incoming requests](#processing-incoming-requests)
  * [Controlling interceptors order](#controlling-interceptors-order)
  * [Forwarding notifications to rooms](#forwarding-notifications-to-rooms)
  * [Protecting notifications endpoint](#protecting-notifications-endpoint)
* [Sending messages](#sending-messages)
  * [Using Symphony standard templates](#using-symphony-standard-templates)  
* [Extension applications](#extension-applications)
  * [Extension app authentication](#extension-app-authentication)
  * [Exposing new endpoints](#exposing-new-endpoints)
  * [Protecting endpoints](#protecting-endpoints)
  * [Symphony clients](#symphony-clients)
  * [Serving the extension app](#serving-the-extension-app)
  * [Testing your app](#testing-your-app)
    * [Streams details endpoint](#streams-details-endpoint)
    * [Users details endpoint](#users-details-endpoint)
    * [Extension app log endpoint](#extension-app-log-endpoint)
    * [Static content](#static-content)
  * [Automatic endpoint documentation](#automatic-endpoint-documentation)
* [Real-Time events](#real-time-events)
  * [Publishing events](#publishing-events)
  * [Subscribing to event types](#subscribing-to-event-types)
  * [Event stream mapping](#event-stream-mapping)
  * [Filtering events](#filtering-events)
* [Monitoring Tools](#monitoring-tools)
  * [Extending health details](#extending-health-details)
  * [Extending Prometheus details](#extending-prometheus-details)
* [Advanced settings](#advanced-settings)
  * [Custom truststore](#custom-truststore)
  * [Proxy support](#proxy-support)
  * [Logging](#logging)
  * [Access control](#access-control)
  * [CORS](#cors)
  * [Cross-site scripting](#cross-site-scripting)
  * [Request origin header](#request-origin-header)
  * [Rate limit](#rate-limit)
  * [Settings reference](#settings-reference)


## Getting Started

These instructions will allow you to set up your Symphony Bot SDK based application.


### Prerequisites
* JDK 1.8
* Maven 3.0.5+
* Service account configured in Symphony Admin portal
* Extension app setup in Symphony Admin portal (optional)


### Setting the service account

In order to register a service account in Symphony Admin Console, a RSA key pair is required. The SDK uses the private key while Symphony needs to know the public one.

In addition to the RSA keys, make sure the property botUsername (and appId) in src/main/resources/bot-config.json file matches the value configured in Symphony Admin Console.


### POD configuration

In src/main/resources/bot-config.json you will find configuration properties where you can specify the details of your 
POD. Fill out the following properties to make the SDK to point to your POD.

|            Property            |                Description                |
|--------------------------------|-------------------------------------------|
| sessionAuthHost                | The session authorization host URL        |
| sessionAuthPort                | The session authorization host port       |
| keyAuthHost                    | The key manager authorization host URL    |
| keyAuthPort                    | The key manager authorization host port   |
| podHost                        | The Pod host URL                          |
| podPort                        | The Pod host port                         |
| agentHost                      | The Agent host URL                        |
| agentPort                      | The Agent host port                       |
| appId                          | The extension app identification          |
| appPrivateKeyPath              | The extension app private key path        |
| appPrivateKeyName              | The extension app private key file        |
| botPrivateKeyPath              | The bot private key path                  |
| botPrivateKeyName              | The bot private key file                  |
| botUsername                    | The bot username                          |
| authTokenRefreshPeriod         | The authentication token refresh period   |
| showFirehoseErrors             | Showing Firehose errors flag              |

### Running locally

The Symphony Bot SDK is built using the Spring Boot and uses Maven to manage the dependencies.

1st Step - Install all of the project's dependencies
```
mvn clean install
```
2st Step - Run the application using Maven and Spring Boot
```
mvn clean spring-boot:run
```
To run it in debug mode, provide the following parameters:
```
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```
Bind the IDE of your choice the the specified port (e.g. 5005).


### Verify your setup

Once the application is up and running, you can check if all the setup works properly by navigating to the health check endpoint: http(s)://&lt;hostname&gt;:&lt;port&gt;/&lt;application_context&gt;/monitor/health.

It should return something like:

```javascript
{
    "status":"UP",
    "details":{
        "symphony":{
            "status":"UP",
            "details":{
                "Symphony":{
                    "agentConnection":"UP",
                    "podConnection":"UP",
                    "agentToPodConnection":"UP",
                    "agentToKMConnection":"UP",
                    "podVersion":"1.55.3",
                    "agentVersion":"2.55.9",
                    "agentToPodConnectionError":"N/A",
                    "agentToKMConnectionError":"N/A",
                    "symphonyApiClientVersion":"1.0.49"
                }
                
            }
            
        },
        "internetConnectivity":{
            "status":"UP",
            "details":{
                "connectivity":"UP"
            }            
        }       
    }    
}

```

 
## Testing commands

Sample commands are shipped with the SDK as a way to assist developers to understand the mechanics of the SDK.
If SDK is properly configured to point to your POD, create an IM or chat room with the bot (search it by the display name you configured in Symphony Admin portal).

All the sample commands require mentioning the bot (e.g. @MyBot), although you can specify any other pattern when creating your own commands.


### Help command

Displays static help message with all available commands

>&#9679; **John Doe**
>
>@MyBot /help

>&#9679; **MyBot**
>
>Bot Commands
>- **@MyBot** /hello - simple hello command
>- **@MyBot** /help - displays the list of commands
>- **@MyBot** /create notification - generates details on how to receive notification in this room
>- **@MyBot** /login - returns the HTTP authorization header required to talk to external system
>- **@MyBot** /quote BRL - returns quote for the specified currency (e.g. BRL)
>- **@MyBot** /register quote - displays the currency quote registration form
>- **@MyBot** /template alert - renders predefined templates (e.g. alert, notification) based on your inputs
>- **@MyBot** /broadcast message - spread a message to all bot active rooms


### Hello command

Simple hello world command.

>&#9679; **John Doe**
>
>@MyBot /hello

>&#9679; **MyBot**
>
>Hello, **John Doe**


### Create notification command

Returns instructions that you can use to receive notifications from external systems into the given Symphony room. To test it, submit a HTTP POST request to the returned URL.

>&#9679; **John Doe**
>
>@MyBot /create notification

>&#9679; **MyBot**
>
>| Method | Request URL |
>|--|--|
>| POST | http://localhost:8080/myproject/notification/GhaWqOo6jRsHv5adBv4q73___pK2eM94dA |
>
>| Header name | Header value |
>|--|--|
>| Accept | application/json |
>| Content-type | application/json |
>
>**Payload**
>
>Click to expand the sample payload


### Login command

Returns the HTTP header required to perform authenticated requests to external systems. Sample code includes two implementations of the AuthenticationProvider interface representing Basic and OAuth v2 authentication.

>&#9679; **John Doe**
>
>@MyBot /login

>&#9679; **MyBot**
>
>**User authenticated**. Please add the following HTTP header to your requests:
>
>```Authorization: Basic am9obi5kb2VAc3ltcGhvbnkuY29tOnN0cm9uZ3Bhc3M=```


### Quote command

Relies on the RestClient library offered by the SDK to request quotes for foreigner currencies on a external system.

>&#9679; **John Doe**
>
>@MyBot /quote BRL

>&#9679; **MyBot**
>
>>USD-BRL X-RATE
>>
>>**3.99**<sub>BRL</sub>


### Template command

Renders messages using Symphony standard templates. Supported values by this sample command are:
* simple
* alert
* information
* notification
* list
* table

>&#9679; **John Doe**
>
>@MyBot /template simple {"title": "Title", "content": "Content"}

>&#9679; **MyBot**
>
>![simple template](readme/template_simple.png)

>&#9679; **John Doe**
>
>@MyBot /template alert {"title": "Title", "content": "Content"}

>&#9679; **MyBot**
>
>![alert template](readme/template_alert.png)

>&#9679; **John Doe**
>
>@MyBot /template information {"title": "Title", "content": "Content", "description": "Description"}

>&#9679; **MyBot**
>
>![information template](readme/template_information.png)

For more information about Symphony standard templates, refer to [Using Symphony standard templates](#using-symphony-standard-templates) section.


### Register quote command

Explores the Symphony Elements visual components to display a form for quote registration in Symphony chat.

>&#9679; **John Doe**
>
>@MyBot /register quote

>&#9679; **MyBot**
>
>![symphony elements form](readme/quote_registration_form.png)


### Attachment command

Highlights message attachments manipulation. Sample code explores downloading attachments content.

>&#9679; **John Doe**
>
>@MyBot /attachment
>
>>attachment1.png (6.47 kB)
>>
>>attachment2.png (12.47 kB)

>&#9679; **MyBot**
>
>![attachment](readme/attachment.png)


### Broadcast command

Spread a message to all bot active rooms.

In the room the command was posted, a messCommand initializationage informing the broadcast rooms must be generated, like the following example:

>&#9679; **John Doe**
>
>@MyBot /broadcast hey guys!
>
>>attachment1.png (6.47 kB)
>>
>>attachment2.png (12.47 kB)

>&#9679; **MyBot**
>
>![broadcast_command](readme/broadcast_command_response.png)

In the broadcast rooms, the message content must be posted, like the example above:

>&#9679; **MyBot**
>
>![attachment](readme/broadcast_message.png)
 

### Default response

The SDK also ships with a mechanism for default responses which sends a default response message in Symphony chat when bot receives an unknown command.

>&#9679; **John Doe**
>
>@MyBot /make coffee

>&#9679; **MyBot**
>
>Sorry, I could not understand


## Testing events

Similarly to commands, Symphony Bot SDK offers a sample code for event handling. When a user is added to a room with the bot, it sends a greeting message mentioning that person:

> Hello, **John Doe**


## Testing notifications

Symphony Bot SDK delivers all support to receive notifications from external systems by exposing an endpoint and offering mechanisms to process incoming requests, the notification interceptors.

A sample notification interceptor is shipped with the SDK. It simply forwards any JSON payload received by the notification endpoint to the Symphony chat specified in URL path.

To test it follow the instructions of the create notification command. Once the POST request comes in, the JSON payload is printed in the specified Symphony room:

>&#9679; **MyBot**
>
>**Notification received:**

```javascript
{"alert": false,"title": "Something Interesting occurred!","content": {"header": "This is an example of a notification, expand to see more","body": "The SDK comes with ready-to-use message templates that you can use to render messages with your own data. You can add you own templates using the extension application."},"showStatusBar": true,"comment": {"body": "so interesting!"},"description": "this is a brief description","assignee": {"displayName": "John Doe"},"type": {"name": "sample"},"status": {"name": "Awesome"},"priority": {"name": "normal"},"labels": [{"text": "Example"},{"text": "SDK"},{"text": "MS"}]}
```


## Adding bot commands

Easily add commands to your bot by extending the ```CommandHandler``` class (or its subclasses ```AuthenticatedCommandHandler```, ```DefaultCommandHandler``` more on them later).

To extend ```CommandHandler``` implement the following methods:

* **Predicate&lt;String&gt; getCommandMatcher()**: use regular expression to specify the pattern to be used by the SDK to look for commands in Symphony messages.

* **void handle(BotCommand command, SymphonyMessage response)**: where you add your business logic to handle the command. This method is automatically called when a Symphony message matches the specified command pattern. Use the ```BotCommand``` object to retrieve the command details (e.g. user which triggered it, room where the command was triggered, the raw command line, etc). Use the ```SymphonyMessage``` object to format the command response. The SDK will take care of delivering the response to the correct Symphony room.   

```java
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " /hello$")
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage response) {
    Map<String, String> variables = new HashMap<>();
    variables.put("user", command.getUserDisplayName());

    response.setTemplateMessage("Hello, <b>${user}</b>", variables);
  }
  
```


### Command initialization

To initialize your own logic prior to ```CommandHandler``` bootstrap, override the following method:

* **void init()**: Initializes the instance dependencies.

```java
    @Override
      public void init() {
        // Initialization logic
      }
```


### Default responses

Typically bots reply to invalid commands with a default friendly message. Extend the ```DefaultCommandHandler``` class to add that behavior to your bots.

Similarly to its base class (i.e. ```CommandHandler```), in ```DefaultCommandHandler``` you will need to provide implementation for both ```getCommandMatcher``` and ```handle``` methods.

Use simple regular expressions to make sure the message was targeted to the bot.

```java
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName())
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage response) {
    response.setMessage("Sorry, I could not understand");
  }

```


### Multi response command handler

Some bots may also need to send custom messages to different rooms. Extend the ```MultiResponseCommandHandler``` class to add that behavior to your bots.

Similarly to its base class (i.e. ```CommandHandler```), in ```MultiResponseCommandHandler``` you will need to provide implementation for both ```getCommandMatcher``` and ```handle``` methods.

Use ```MultiResponseComposer``` to compose your messages.

```java
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
      .compile("^@" + getBotName() + "/compose$")
      .asPredicate();
  }

  @Override
  public void handle(BotCommand command, MultiResponseComposer multiResponseComposer) {
    multiResponseComposer.compose()
      .withMessage("message")
      .toStreams("stream1", "stream2")
      .withTemplateFile("template", getTemplateParam())
      .withAttachments(getAttachments())
      .toStreams("stream3", "stream4")
      .complete();
  }

```


### Authenticating to external system

When integrating with external systems, bots generally need to consume APIs exposed by such systems which require some sort of authentication. 

Symphony Bot SDK provides mechanisms to support you on that. Through its ```AuthenticationProvider``` interface and ```AuthenticatedCommandHandler``` class the SDK offers:

* separation of concerns: isolate the authentication logic from command business logic
* code reuse: single authentication method, multiple commands
* rapidly replace the authentication method: ```AuthenticationProvider``` implementation changes, commands remain

```java
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " /login$")
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse,
      AuthenticationContext authenticationContext) {

    commandResponse.setMessage("<b>User authenticated</b>. "
        + "Please add the following HTTP header to your requests:"
        + "Authorization: "
        + authenticationContext.getAuthScheme() + " "
        + authenticationContext.getAuthToken());
  }
 
```


#### AuthenticationProvider

To leverage the authentication support offered by the SDK, provide an implementation of the ```AuthenticationProvider``` interface.

The ```AuthenticationProvider``` interface defines two methods:

* **AuthenticationContext getAuthenticationContext(String userId)**: returns an ```AuthenticationContext``` object which holds authentication details for the given Symphony user.  

* **void handleUnauthenticated(BotCommand command, SymphonyMessage commandResponse)**: invoked when the corresponding Symphony user is still not authenticated to the external system.


#### AuthenticatedCommandHandler

The ```AuthenticatedCommandHandler``` is a specialization of ```CommandHandler``` which interacts with ```AuthenticationProvider``` to retrieve an ```AuthenticationContext``` before invoking the ```handle``` method. All the authentication process is abstracted away from the command handler.

If the Symphony user issuing the command is still not authenticated to the external system,  ```AuthenticatedCommandHandler``` will defer to the ```handleUnauthenticated``` method in ```AuthenticationProvider``` and the ```handle``` method will not be invoked.

The ```handle``` method in ```AuthenticatedCommandHandler``` child classes receives an extra parameter, the ```AuthenticationContext``` which contains necessary details to make authenticated requests to the external system.

**Notice:** the SDK supports multiple ```AuthenticationProvider```. When only one implementation of the ```AuthenticationProvider``` interface is provided, the SDK will automatically inject it to all ```AuthenticatedCommandHandler``` child classes. Otherwise, you will have to specify which ```AuthenticationProvider``` to use with each ```AuthenticatedCommandHandler``` by annotating the command handlers with the ```CommandAuthenticationProvider``` annotation.

```java
@CommandAuthenticationProvider(name="BasicAuthenticationProvider")
public class LoginCommandHandler extends AuthenticatedCommandHandler {
```


### Command Matcher

In order to avoid writing complex regular expression when specifying the command pattern, the developer can also use ```CommandMatcherBuilder```.

For example, the following pattern:

```java
  Pattern
    .compile("^@BotName\\s/template(?:\\s+(?:([^\\s]+)(?:\\s+([\\s\\S]+)?)?)?)?")
    .asPredicate();
```
would be built this way with ```CommandMatcherBuilder```:

```java
  beginsWith("@")
    .followedBy("BotName")
    .followedBy(whiteSpace())
    .followedBy("/template")
    .followedBy(
      optional(
        nonCapturingGroup(
          oneOrMore(whiteSpace()
          ).followedBy(
            optional(
              nonCapturingGroup(
                group(
                  oneOrMore(
                    negatedSet(whiteSpace())
                    )
                ).followedBy(
                  optional(
                    nonCapturingGroup(
                      oneOrMore(whiteSpace()
                      ).followedBy(
                        optional(
                          group(
                            oneOrMore(any())
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    ).pattern();
```


## Handling Symphony events

Bots may need to react to events happening on Symphony rooms they are part of (e.g. sending a greeting message to users who join the room).

Similarly to commands, Symphony Bot SDK offers straightforward mechanisms for your bots to be notified when something happens on Symphony chats. By extending the ```EventHandler``` class you register your bot to react to a specific Symphony event.

To extend ```EventHandler``` you need to: 

* specify the event type: ```EventHandler``` is a parameterized class so you need to specified which event type you want to handle. *Refer to the following subsection for the list of supported events*.
 
* implement **void handle(&lt;symphony_event&gt; event, final SymphonyMessage eventResponse)**: this is where you add your business logic to handle the specified event. This method is automatically called when the specified event occurs in a room where the bot is. Use the ```event``` object to retrieve event details (e.g. target user or target room). Use the ```SymphonyMessage``` object to format the event response. The SDK will take care of delivering the response to the correct Symphony room.

```java
public class UserJoinedEventHandler extends EventHandler<UserJoinedRoomEvent> {

  @Override
  public void handle(UserJoinedRoomEvent event, SymphonyMessage response) {
    response.setMessage("Hey, <mention uid=\"" + event.getUserId() +
        "\"/>. It is good to have you here!");
  }

```

**Notice:** Different ```EventHandler``` child classes can handle the same event. All of them will have their ```handle``` method called. There is no way to set the calling order. 
  

### Available Symphony events

* **IMCreatedEvent**: fired when an IM is created with the bot
* **RoomCreatedEvent**: fired when a room is created with the bot
* **RoomDeactivatedEvent**: fired when room is deactivated
* **RoomReactivatedEvent**: fired when room is reactivated
* **RoomUpdatedEvent**: fired when room is updated
* **UserJoinedRoomEvent**: fired when user joins a room with the bot
* **UserLeftRoomEvent**: fired when user lefts a room with the bot
* **RoomMemberDemotedFromOwnerEvent**: fired when a room member is demoted from room ownership
* **RoomMemberPromotedToOwnerEvent**: fired when a room member is promoted to room owner


### Permission for bots in public rooms 

Symphony Bot SDK offers an easy way to control whether your bots are allowed to be added to public rooms or not.

By default, bots built with the Symphony Bot SDK are able to join public rooms. To change that behavior, the developer just needs to set the ```isPublicRoomAllowed``` in application-feature.yaml file.

It is also possible to configure a custom message the bot would send before quitting the room, through the following configurations:

| Property                                 | Description                                                                  |
|------------------------------------------|------------------------------------------------------------------------------|
| features.isPublicRoomAllowed             | Whether bot is allowed in public rooms                                       |
| features.publicRoomNotAllowedMessage     | Message displayed before the bot leaves the room                             |
| features.publicRoomNotAllowedTemplate    | Template file with the message displayed before the bot leaves the room      |
| features.publicRoomNotAllowedTemplateMap | Template parameters of the message displayed before the bot leaves the room  |

Developers can specify simple static message to be displayed before the bot leaves the room, by setting  ```publicRoomNotAllowedMessage```, like the example below:

 ```yaml
features:
  isPublicRoomAllowed: false
  publicRoomNotAllowedMessage: Sorry, I cannot be added to public rooms
```

or, they can use templates to generate structured messages, by setting ```publicRoomNotAllowedTemplate``` and ```publicRoomNotAllowedTemplateMap```, like the following example:

 ```yaml
features:
  isPublicRoomAllowed: false
  publicRoomNotAllowedTemplate: alert
  publicRoomNotAllowedTemplateMap:
    message:
      title: Bot not allowed in public rooms
      content: Sorry, I cannot be added to public rooms
```

## Working with Symphony Elements

Symphony Elements allow bots to send messages containing interactive forms with text fields, dropdown menus, person selectors, buttons and more.

Symphony Bot SDK fully supports Elements. By extending the ```ElementsHandler``` class you get all you need to handle Symphony Elements, from the command to display the Elements form in a chat room to the callback triggered when the Symphony Elements form is submitted. All in one single class.

To extend ```ElementsHandler``` you need to implement the following methods:

* **Predicate&lt;String&gt; getCommandMatcher()**: similar to ```CommandHandler```. Use regular expression to specify the pattern to be used by the SDK to look for commands in Symphony messages.

* **String getElementsFormId()**: returns the Symphony Elements form ID.

* **void displayElements(BotCommand command, SymphonyMessage elementsResponse)**: This is where you add your logic to render the Symphony Elements form. Similarly to ```CommandHandler```, this method is automatically called when a Symphony message matches the specified command pattern. Use the ```BotCommand``` object to retrieve the command details (e.g. user which triggered it, room where the command was triggered, the raw command line, etc). Use the ```SymphonyMessage``` object to format the Symphony Elements form. The SDK will take care of delivering the response to the correct Symphony room.

* **void handleAction(SymphonyElementsEvent event, SymphonyMessage elementsResponse)**: where you handle interactions with the Elements form. This method is automatically called when users submit the Elements form. The ```SymphonyElementsEvent``` holds details about the action performed on the form (e.g. form payload, action name, etc). Use the ```SymphonyMessage``` object to format a response according to the Elements form action.

```java
  private static final String FORM_ID = "quo-register-form";
  private static final String FROM_CURRENCY = "fromCurrency";
  private static final String TO_CURRENCY = "toCurrency";
  private static final String AMOUNT = "amount";
  private static final String ASSIGNED_TO = "assignedTo";

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /register quote$")
        .asPredicate();
  }

  @Override
  protected String getElementsFormId() {
    return FORM_ID;
  }

  @Override
  public void displayElements(BotCommand command,
      SymphonyMessage elementsResponse) {
    Map<String, String> data = new HashMap<>();
    data.put("form_id", getElementsFormId());
    elementsResponse.setTemplateFile("quote-registration", data);
  }

  @Override
  public void handleAction(SymphonyElementsEvent event,
      SymphonyMessage elementsResponse) {
    Map<String, Object> formValues = event.getFormValues();
    
    Map<String, Object> data = new HashMap<String, Object>();
    data.put(FROM_CURRENCY, formValues.get(FROM_CURRENCY));
    data.put(TO_CURRENCY, formValues.get(TO_CURRENCY));
    data.put(AMOUNT, formValues.get(AMOUNT));
    data.put(ASSIGNED_TO, event.getUser().getDisplayName());

    elementsResponse.setTemplateMessage(
        "Quote FX {{fromCurrency}}-{{toCurrency}} {{amount}} sent to dealer {{assignedTo}}", data);
  }

```

Sample [Handlebars](https://github.com/jknack/handlebars.java)-based template for the quote registration form:
```xml
<form id="{{form_id}}">
  <h3>Quote Registration</h3>
  <h6>From currency</h6>
  <text-field minlength="3" maxlength="3" masked="false" name="fromCurrency" required="true"></text-field>
  <h6>To currency</h6>
  <text-field minlength="3" maxlength="3" masked="false" name="toCurrency" required="true"></text-field>
  <h6>Amount</h6>
  <text-field minlength="1" maxlength="9" masked="false" name="amount" required="true"></text-field>
  <h6>Assigned To:</h6>
  <person-selector name="assignedTo" placeholder="Assign to.." required="false" />
  <h6>Quote Status:</h6>
  <radio name="status" checked="true" value="pending">Pending</radio>
  <radio name="status" checked="false" value="confirmed">Confirmed</radio>
  <radio name="status" checked="false" value="settled">Settled</radio>
  <h6>Remarks:</h6>
  <textarea name="remarks" placeholder="Enter your remarks.." required="false"></textarea>
  <button name="confirm" type="action">Confirm</button>
  <button name="reset" type="reset">Reset</button>
</form>
```

**Notice:** The ```event.getFormValues()``` returns a map with the values of all input fields in the Symphony Elements form. The key names of that map match the HTML ```name``` property of the input elements in Elements form. Also, the ```id``` property of the form must match the value returned by ```getElementsFormId```.  


### ElementsActionHandler

For scenarios where the Symphony Elements form is not generated through a command targeted to your bot (e.g. a user interacting with an extension app, a notification from external system) but you need to handle the interactions with that form, extend the ```ElementsActionHandler``` class rather than ```ElementsHandler```. 

```ElementsActionHandler``` is actually an ```EventHandler``` and therefore is simpler and easier to extend than ```ElementsHandler```. It just requires implementing the ```getElementsFormId``` and ```handle``` methods.


## Receiving notifications

Receiving notifications from external systems directly into Symphony chats is another common use case for bots and Symphony Bot SDK delivers all the support you need by:

1. Exposing the ```/notification``` endpoint through which external systems can send their events: http(s)://&lt;hostname&gt;:&lt;port&gt;/&lt;application_context&gt;/notification

2. Offering mechanisms for you to register your own logic to process incoming notification requests through the ```NotificationInterceptor``` class, including discarding requests when applicable 
   
3. Sending notification contents to Symphony rooms

4. Protecting the ```/notification``` endpoint


### Processing incoming requests

Incoming requests can be easily processed by extending the ```NotificationInterceptor``` class. The way Symphony Bot SDK supports processing notifications mimics the Filter idea of the Java Servlet specification, that is, you can chain multiple ```NotificationInterceptor``` classes together each one tackling a different aspect of the request, but all of them collaborating to either process or discard the request.

Extending ```NotificationInterceptor``` class will automatically register your interceptor to the SDK internal ```InterceptorChain```.

To create your own ```NotificationInterceptor``` you simply need to implement the following method:

* **boolean process(NotificationRequest notificationRequest, SymphonyMessage notificationMessage)**: where you add your business logic to process incoming requests (e.g. HTTP header verification, JSON payload mapping, etc). Use the ```NotificationRequest``` to retrieve all details of the notification request (e.g. headers, payload, identifier). You can also use its ```getAttribute```/```setAttribute``` methods to exchange data among your interceptors. Use the ```SymphonyMessage``` object to format the notification. The SDK will take care of delivering the response to the correct Symphony room. This method is automatically called for each notification request. Return false if the request should be discarded, true otherwise. 

```java
  @Override
  public boolean process(NotificationRequest notificationRequest, SymphonyMessage notificationMessage) {

    // For simplicity of this sample code identifier == streamId
    String streamId = notificationRequest.getIdentifier();

    if (streamId != null) {
      notificationRequest.setStreamId(streamId);
      notificationMessage.setMessage(
          "<b>Notification received:</b><br />" + notificationRequest.getPayload());
      return true; // true if notification interception chain should continue
    }

    return false; // false if notification intercept chain should be halted and request rejected
  }

```


### Controlling interceptors order

If you need to specify multiple request interceptors and want to control their execution order, extend the ```OrderedNotificationInterceptor``` rather than ```NotificationInterceptor``` and implement the ```getOrder()``` method.

 
### Forwarding notifications to rooms

The notification support offered by the SDK uses an extra path parameter (that is, ```/notification/<some_value>```) to identify which room a particular notification should be sent to. That extra parameter is internally called as 'identifier' and can be retrieved from the ```NotificationRequest``` object.

By default the SDK assumes the 'identifier' is the stream ID of the room. If for your scenario 'identifier' means something else or you have a completely different mechanism to identify the room, you must set the stream ID in ```NotificationRequest``` manually.  


### Protecting notifications endpoint

Notification endpoint is public by default. Nevertheless Symphony Bot SDK has a built-in IP whitelisting mechanism that could be easily set up to allow only specific IP addresses or IP ranges to have access that endpoint.

You can enable and configure that mechanism by adding the following in ```application-dev.yaml``` file:

```yaml
access-control:
  ipWhitelist: <comma-separated IP list>
  urlMapping: "/notification"
```

## Sending messages

The ```SymphonyMessage``` object holds the details for a message to be sent to Symphony. It offers the following different ways to specify the message content:

* **void setMessage(String message)**: specifies a static message to be displayed in a Symphony room.

* **void setTemplateMessage(String templateMessage, Object templateData)**: automatically interpolates a string with template wildcards using the given data object. 

* **void setTemplateFile(String templateFile, Object templateData)**: automatically loads the specified template file and interpolates its content using the given data object. Template files must be placed in your resources directory under ```templates```. 

* **void setEnrichedMessage(String message, String entityName, Object entity, String version)**: similar to ```setMessage``` but offers data to for an extension app to create enriched messages replacing what has been specified as ```message```. If no extension app is registered, the ```message``` gets displayed.

* **void setEnrichedTemplateMessage(String templateMessage, Object templateData, String entityName, Object entity, String version)**: similar to ```setTemplateMessage``` but offers data to for an extension app to create enriched messages replacing what has been specified as ```templateMessage```. If no extension app is registered, the interpolated ```templateMessage``` gets displayed.

* **void setEnrichedTemplateFile(String templateFile, Object templateData, String entityName, Object entity, String version)**: similar to ```setTemplateFile``` but offers data to for an extension app to create enriched messages replacing what has been specified as ```templateFile```. If no extension app is registered, the interpolated content of ```templateFile``` gets displayed.
  
Symphony Bot SDK is shipped with [Handlebars](https://github.com/jknack/handlebars.java) template engine and automatically handles the template processing for you.


### Using Symphony standard templates

Symphony Bot SDK integrates seamlessly with [SmsRenderer](https://github.com/SymphonyPlatformSolutions/sms-sdk-renderer-java) tool to offer predefined message templates.

The file-based methods in ```SymphonyMessage``` (```setTemplateFile``` and ```setEnrichedTemplateFile```) can be used to render such templates. For that, you just need to specify the predefined template from ```SmsRenderer.SmsTypes``` enum.

Example:

```java
public class TemplateSampleHandler extends CommandHandler {

  @Override
  protected Predicate<String> getCommandMatcher() {
    ...
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    Map<String, Object> commandParameter =
        jsonMapper.toObject("{\"message\": {\"title\": \"Title\", \"content\": \"Content\"}}", Map.class);
    commandResponse.setTemplateFile(SmsRenderer.SmsTypes.ALERT.getName(), commandParameter);
  }

}
```

Currently, Symphony Bot SDK offers the following templates:

* ALERT
* INFORMATION
* LIST
* NOTIFICATION
* SIMPLE
* TABLE

For more information about the Symphony standard templates, take a look on https://github.com/SymphonyPlatformSolutions/sms-sdk-renderer-java. Also, check [Template command](#template-command) section.


## Extension applications

In addition to all support for bots development, Symphony Bot SDK also comes with great tools to streamline the Symphony-extension apps integration process.


### Extension app authentication

The extension app authentication process spawns three steps which aim to establish a bidirectional trust between an application and Symphony.

Symphony Bot SDK removes all the complexity related to the authentication process by exposing the following endpoints through which an application can authenticate itself:

| Method | Endpoint | Description | Request | Response
|---|---|---|---|---|
| POST | /application/**authenticate** | Initiates the authentication process. Extension app sends its application ID, retrieved from Symphony Client APIs, which must match the same ID configured in Symphony Admin portal. An application token is returned to the extension app. |  `{"appId": "myAppId"}` | `{"appId": "myAppId", "appToken": "bde...e"}` |
| POST | /application/**tokens/validate** |  Validates the application and Symphony tokens generated in previous step. Extension app provides the Symphony token, obtained through Symphony Client APIs. | `{"appToken":"05b...c",` `"symphonyToken":"0...6",` `"appId":"myAppId"}` | 200 OK if tokens valid, 401 Unauthorized otherwise |
| POST | /application/**jwt/validate** | Validates a signed JWT holding user details  |  `{"jwt":"ey...g"}` | the user ID |

Extension apps must rely on those three endpoints in the order they are described to get authenticated to Symphony. If any of those steps fails, the authentication fails and extension app will not be launched (i.e. not displayed on the left-nav menu).


### Exposing new endpoints

The web support in Symphony Bot SDK is based on SpringMVC framework. So exposing endpoints for your extension apps requires:

* Annotating your classes with ```@Controller``` or ```@RestController```
* Mapping your routes using ```@RequestMapping```, ```@GetMapping```, ```@PostMapping```, etc

```java
@RestController
@RequestMapping("/secure/myendpoint")
public class MyController {

  @GetMapping
  public ResponseEntity getSomeData() {
    // ... logic to retrieve data
    return ResponseEntity.ok(new Object());
  }

  @PostMapping
  public ResponseEntity setSomeData(@RequestBody String data) {
    // ... logic to set data
    return ResponseEntity.ok();
  }
}

```


### Protecting endpoints

When exposing endpoints for extension apps you will likely need to restrict access to them.

Symphony Bot SDK offers a simple way for you to protect endpoints so that only your applications would have access to them. All endpoints exposed under ```/secure/``` path are automatically protected.

To access them, Symphony Bot SDK requires requests to have the HTTP authorization header set with a valid JWT: 

```Authorization: Bearer eyJ...ybxRg```

It is possible to configure a different value for the secure path. In ```bot-config.json``` change the following property:

```jvascript
  "authenticationFilterUrlPattern": "/secure/",
```

Be sure to reflect your change to all of your controllers.


### Symphony clients

Typically extension apps deliver features that involve retrieving/persisting data from/to Symphony. Symphony Bot SDK provides the building blocks for such features, the Symphony clients.

The following clients are available:

* ```MessageClient```: offers ways to send messages to Symphony rooms
* ```StreamsClient```: retrieves streams and rooms details and manages rooms
* ```UsersClient```: retrieves user details

```java
  private final StreamsClient streamsClient;

  public StreamsController(StreamsClient streamsClient) {
    this.streamsClient = streamsClient;
  }

  @GetMapping
  public ResponseEntity<List<SymphonyStream>> getUserStreams() {
    try {
      return ResponseEntity.ok(streamsClient.getUserStreams(null, true));
    } catch (SymphonyClientException sce) {
      // ... handling communication failure with Symphony
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

```

**Notice:** For any communication issue with Symphony a ```SymphonyClientException``` is raised. Handle that exception properly to improve user experience.


### Serving the extension app

Static assets of an extension app (e.g. Javascript, CSS, images, HTML) can be served either in a separated host or along with the bundle you generate for your Symphony Bot SDK.

If you plan to have a different server for you web UI, make sure CORS is properly configured. In ```application-dev.yaml``` add the following properties:

```yaml
cors:
  allowed-origin: "<web UI domain>"
  url-mapping: "/**"

```

To distribute the extension app as part of your Symphony Bot SDK based application, place all of you static assets under: ```<symphony bot application base path>/src/main/resources/public``` and build your application. In this case, all of your assets will be under ```/app/``` path. Example,  my_image.png file placed under ```public``` directory would be accessible in the following URL:

```http(s)://<hostname>:<port>/<application_context>/app/my_image.png```

**Notice:** When registering your extension apps in Symphony Admin portal, make sure you take the ```/app/``` into account when setting the load URL.


### Testing your app

Symphony Bot SDK ships with few endpoints to assist developers on understanding how to leverage Symphony Bot SDK to create their own extension apps. All endpoints are protected and require extension app to be authenticated.

Please refer to following sub-sections for more details.


#### Streams details endpoint

| Method | URL | Description
|---|---|---|
| GET | /secure/streams | Returns a list of streams the bot is part of

**Response**

```javascript
[
  {
    "active": true,
    "crossPod": true,
    "members": [
      7215545078229
    ],
    "roomName": " my room name",
    "streamId": "iWyZBIOdQQzQj0tKOLRivX___qu6YeyZdA",
    "streamType": "ROOM"
  }
]
```


#### Users details endpoint

| Method | URL | Description
|---|---|---|
| GET | /secure/users | Returns user details by username or user ID. Set either ```username``` or ```userId``` request parameter.

**Response**

```javascript
{
  "userId": 15942919536460,
  "emailAddress": "test_1@symphony.com",
  "firstName": "test_1",
  "lastName": "test",
  "displayName": "test_1 test",
  "title": "Technical Writer",
  "company": "pod232",
  "username": "test_1",
  "location": "location",
  "avatars": [
    {
      "size": "original",
      "url": "../avatars/static/150/default.png"
    }
  ]
}

```


#### Extension app log endpoint

| Method | URL | Description
|---|---|---|
| POST | /secure/log | Persists extension apps logs along with server-side logs. Set ```level``` request parameter to change log level

**Request**

```javascript
"a log message from frontend"

```


#### Static content

Under ```<symphony bot application base path>/src/main/resources/public``` you will find a sample Symphony logo (logo.svg).
 
Try accessing it from:
* browser (full path URL): ```http(s)://<hostname>:<port>/<application_context>/app/logo.svg```
* your extension app code (relative path): ```./logo.svg```


### Automatic endpoint documentation

Symphony Bot SDK is shipped with [Swagger](https://swagger.io/tools/open-source/getting-started/) already configured. Documentation for all existing endpoints and new ones that you may add can be found here:

```
http(s)://<hostname>:<port>/<application_context>/swagger-ui.html

```


## Real-Time events

With Symphony Bot SDK your extension apps can quickly leverage real-time events. Based on the Server-Sent Event (SSE) technology, Symphony Bot SDK delivers real-time events support through the following components: 

* ```SseController``` which exposes the endpoint through which extension applications subscribe for real-time events 
* ```SsePublisher```, a base class to publish real-time events
* ```SseSubscriber```, an abstraction of clients subscribing for events   


### Publishing events

```SsePublisher``` child classes represent the bridge between your business logic and the client applications listening to your events. Create as many publishers as you need according to the event types they should publish.

The ```SsePublisher``` class is parameterized to allow you to provide any kind of data in ```publishEvent``` as long as you implement the ```SsePublishable``` interface.

To extend ```SsePublisher``` implement the following methods:

* **List&lt;String&gt; getEventTypes()**: returns a list with event types that this particular publisher is responsible for. Clients must specify the event types they want to listen to in their requests path.

* **void handleEvent(SseSubscriber subscriber, SsePublishable event)**: where you add your logic to process events before publishing them. This method is not publicly visible and your business logic will not call it directly. Rather, your code should call ```publishEvent``` whenever you need to publish an event. Symphony Bot SDK will automatically call ```handleEvent``` for each subscriber of that particular event type. Use the ```SseSubscriber``` object to retrieve details of the clients subscribing for events and to send them your events.   


Optionally, you may consider extending the following methods:

* **void init()**: invoked right after Symphony Bot SDK instantiates your class. Useful for initialization logic.  

* **void onSubscriberAdded(SubscriptionEvent subscriberAddedEvent)**: called when new subscriber registers for event types handled by that particular publisher.

* **void onSubscriberRemoved(SubscriptionEvent subscriberRemovedEvent)**: called when subscriber unregisters for event types handled by that particular publisher.


**Notice:** DO NOT block the thread in ```onSubscriberAdded``` and ```onSubscriberRemoved``` methods.
  

```java
  private static final long WAIT_INTERVAL = 1000L;

  private boolean running = false;
  private int subscribers;

  @Override
  public List<String> getEventTypes() {
    return Stream.of("event1", "event2")
        .collect(Collectors.toList());
  }

  @Override
  protected void handleEvent(SseSubscriber subscriber, SsePublishable event) {
    // For simplicity, just send the event to the client application. In real
    // scenarios you could rely on subscriber.getMetadata to check if client is
    // really interested in this particular event.
    subscriber.sendEvent(event);
  }

  @Override
  protected void onSubscriberAdded(SubscriptionEvent subscriberAddedEvent) {
    // Start simulating event generation on first subscription
    subscribers++;
    if (!running) {
      running = true;
      simulateEvent();
    }
  }

  @Override
  protected void onSubscriberRemoved(SubscriptionEvent subscriberRemovedEvent) {
    subscribers--;

    // Stop simulation if no more subscriber
    if (subscribers == 0) {
      running = false;
    }
  }

  private void simulateEvent() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(() -> {
      int id = 0;
      while (running) {
        id++;

        // Create sse publishable event with payload
        SimpleEvent event = new SimpleEvent();
        event.setId(Integer.toString(id));
        event.setPayload("SSE Test Event - " + LocalTime.now().toString());

        // Simulate event alternation
        event.setType((id % 2) != 0 ? "event1" : "event2");

        // Publish event
        this.publishEvent(event);

        waitForEvents(WAIT_INTERVAL);
      }
    });

  }

  private void waitForEvents(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException ie) {
      LOGGER.debug("Error waiting for next events");
    }
  }

  @Data
  @NoArgsConstructor
  public class SimpleEvent implements SsePublishable {
    private String payload;
    private String type;
    private String id;
  }

```

**Notice:** ```SsePublisher``` exposes the ```publishEvent``` method which must be called by your event generation logic to get events properly published to clients. You may rely on ```complete``` or ```completeWithError``` methods to properly tell client applications your publisher is done and will send no more events.


### Subscribing to event types

| Method | URL | Description
|---|---|---|
| GET | /secure/events/&lt;comma separated event types&gt; | Subscribe to the specified event types. Use query params to add metadata such as filtering criteria.   

**Client sample**

```javascript

// Listening to stock price updates
const evtSource = new EventSource("http://localhost:8080/botapp/secure/events/stockprice");

```


### Event stream mapping

If you have multiple ```SsePublisher``` generating events of different nature (e.g. stock prices and currencies exchange rates), you can name the event types so that subscribers are properly served by the corresponding publishers.

Symphony Bot SDK automatically maps clients requests to the corresponding publishers based on the event types present in the request path (e.g. /stockprice, /x-rate). Event types in client requests must match the ones registered by publishers through ```getEventTypes()``` method.

**Client sample**

```javascript

// Listening to stock price updates only
const evtSource = new EventSource("http://localhost:8080/botapp/secure/events/stockprice");

// Listening to stock price and x-rate updates
const evtSource2 = new EventSource("http://localhost:8080/botapp/secure/events/stockprice,x-rate");


```


### Filtering events

Real-time events may be filtered based on some criteria. All query parameters in a subscription request are handled by Symphony Bot SDK as metadata and forwarded to publishers through ```SseSubscriber``` object.


```javascript

// Listening to Tesla stock price updates
const evtSource = new EventSource("http://localhost:8080/botapp/secure/events/stockprice?name=TSLA");

```

The publisher then checks if there are any specified filters:

```java
  @Override
  protected void handleEvent(SseSubscriber subscriber, SseEvent event) {
    
    ...
    
    Map<String, String> metadata = subscriber.getMetadata();
    if (price.getStockName().equals(metadata.get("filterByName")) {
      // publish SSE event
    }
    
    ...
  }

```


## Monitoring Tools

Symphony Bot SDK comes with production-ready features to help you monitoring your applications when deployed to production.

Such features consist of the following HTTP endpoints that you can use to pull health and other metrics to check the status of your application:

* **/monitor/info**: simple endpoint that returns HTTP 200 OK when application is up
* **/monitor/health**: provides health details. By default, only Symphony-related health details are exposed
* **/monitor/prometheus**: health and metrics details to be consumed by [Prometheus](https://prometheus.io/)


### Extending health details

The Symphony Bot SDK monitoring system is based on Spring Actuators. By default, it exposes the following health metrics:

* **overall system health status**: represented by the top ```status``` field. It shows 'UP' if all other metrics are fine, that is, 'UP'. 
* **symphony**: symphony components metrics. It shows 'UP' only if your bot is properly communicating with the POD and agent and all Symphony components (e.g. agent, Key Manager, POD) are accessible. 

```javascript
{
    "status":"UP",
    "details":{
        "symphony":{
            "status":"UP",
            "details":{
                "Symphony":{
                    "agentConnection":"UP",
                    "podConnection":"UP",
                    "agentToPodConnection":"UP",
                    "agentToKMConnection":"UP",
                    "podVersion":"1.55.3",
                    "agentVersion":"2.55.9",
                    "agentToPodConnectionError":"N/A",
                    "agentToKMConnectionError":"N/A",
                    "symphonyApiClientVersion":"1.0.49"
                }
                
            }
        }        
    }    
}
```

There are many other built-in metrics in Spring Actuator. Please refer to their documentation for enabling those metrics. 

To create your own custom metric you need to implement the Spring Actuator ```HealthIndicator``` interface and use ```Health``` builder to convey your status. Your metrics are automatically integrated with the system ones in the monitoring endpoint: http(s)://&lt;hostname&gt;:&lt;port&gt;/&lt;application_context&gt;/monitor/health.

```java
public class InternetConnectivityHealthIndicator implements HealthIndicator {

  private RestClient restClient;

  public InternetConnectivityHealthIndicator(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public Health health() {
    try {
      restClient.getRequest("https://symphony.com", String.class);
      return Health.up().withDetail("connectivity", "UP").build();
    } catch (Exception e) {
      return Health.down().withDetail("connectivity", "DOWN").build();
    }
  }

}
```

### Extending Prometheus details

Spring Actuator exposes default metrics in Prometheus endpoint. Symphony Bot SDK extends them to also include the communication status of Symphony-related components (e.g. agent, Key Manager, POD).

To expose your own custom details in Prometheus endpoint, you need to implement ```MeterBinder``` interface as follow:

```java
public class SymphonyHealthMeterBinder implements MeterBinder {

  ...
  
  private HealthCheckInfo status() {
    return healthcheckClient.healthCheck();
  }

  @Override
  public void bindTo(MeterRegistry registry) {
    LOGGER.info("Registering Symphony health status to Prometheus endpoint");
    HealthCheckInfo healthStatus = status();

    Gauge.builder(METRIC_NAME, this, value -> value.status().checkOverallStatus() ? 1.0 : 0.0)
        .description(METRIC_DESCRIPTION)
        .tags(Tags.of(
            Tag.of(TAG_POD_VERSION, healthStatus.getPodVersion()),
            Tag.of(TAG_AGENT_VERSION, healthStatus.getAgentVersion()),
            Tag.of(TAG_API_VERSION, healthStatus.getSymphonyApiClientVersion())))
        .baseUnit(BASE_UNIT)
        .register(registry);
  }
```


## Advanced settings

### Custom truststore
If SSL connection to any endpoint uses private or self-signed certificates, add the following properties to the ```bot-config.json``` to tell the SDK which truststore to use:

```javascript
"truststorePath": "/path/to/truststore/",
"truststorePassword": "truststore password",

```


### Proxy support

In case connection to Symphony components (e.g. POD, agent, key manager) requires going through proxies, the following properties can be set in ```bot-config.json```:

```javascript
// If only POD access requires proxy. Username/password only required
// if proxy uses basic authentication
"podProxyURL": "proxy url", 
"podProxyUsername": "username",
"podProxyPassword": "password",

// If access to both POD and agent requires proxy
"proxyURL": "proxy url", 
"proxyUsername": "username",
"proxyPassword": "password",

// If access to key manager requires proxy
"keyManagerProxyURL": "proxy url",
"keyManagerProxyUsername": "username",
"keyManagerProxyPassword: "password",

```

For connections to external systems using the REST client shipped with Symphony Bot SDK define the following properties in ```application-dev.yaml```:

```yaml
restclient:
  proxy:
    address: "proxy hostname"
    port: "proxy port"

```


### Logging

To change log file name and/or location, or to change log level for different packages, set the following properties in ```application-dev.yaml```.

```yaml
logging:
  file: /home/myuser/symphony-bot-sdk-java/logs/bot-app.log
  level:
    ROOT: INFO
    com.symphony.bdk.bot.template: DEBUG

```


### Access control

To protect endpoints using basic authentication and/or IP whitelist, specify the following in ```application-dev.yaml```:

```yaml
access-control:
  name: myusername
  hashedPassword: 5e88489...21d1542d8
  salt: 11111
  ipWhitelist: 192.168.0.155, 192.168.2.178
  urlMapping: /monitor

```

**Notice:** The basic authentication protection is pretty simple allowing only one username/password to be specified.


### CORS

If extension app is running in different host, rely on the CORS support to make Symphony Bot SDK to accept requests coming from the extension app. In ```application-dev.yaml``` add:

```yaml
cors:
  allowed-origin: myextensionapp.com
  url-mapping: /**

```


### Cross-site scripting

Protect endpoint from XSS attacks by setting the following properties in ```application-dev.yaml```:

```yaml
xss:
  url-mapping: /secure/*

```


### Request origin header

It is also possible to protect endpoints based on shared secrets. This is particularly useful when your application needs to receive notifications from external systems and therefore needs to expose a publicly available endpoint.

Both the external system and your application could share a secret through some secure channel. On every notification sent, the external system would add that secret in a HTTP header and Symphony Bot SDK would automatically reject requests without it.

In ```application-dev.yaml``` add the following:

```yaml
request-origin:
  origin-header: x-origin-token
  url-mapping: /notification

```


### Rate limit

It is possible to limit access to your application using the Symphony Bot SDK's throttling mechanism. You need to specify the limit and one of the throttling mode:

* ORIGIN: limits request rate based on origin IP address    
* ENDPOINT: limits request rate per endpoints exposed by your application (default if not specified)

**Notice:** When application is running behind load balancers or firewalls, the calling IP address may be rewritten. Usually such network components keep the original IP address in HTTP headers. Symphony Bot SDK looks for the following headers when throttling in ORIGIN mode:

* X-Forwarded-For
* Proxy-Client-IP
* WL-Proxy-Client-IP
* HTTP_X_FORWARDED_FOR
* HTTP_X_FORWARDED
* HTTP_X_CLUSTER_CLIENT_IP
* HTTP_CLIENT_IP
* HTTP_FORWARDED_FOR
* HTTP_FORWARDED
* HTTP_VIA
* REMOTE_ADDR

Example: specify that the same IP address can issue at most 200 concurrent requests per second

```yaml
throttling:
  limit: 200
  mode: ORIGIN
  timeout: 10000

```

The timeout property is used to define the maximum amount of time a request waits in throttling mechanism before it is processed. If that time exceeds, a HTTP 408 error is returned to the caller.


### Settings reference

|                 Property                 |                                          Description                                         |    Configuration file    |
|------------------------------------------|----------------------------------------------------------------------------------------------|--------------------------|
| truststorePath                           | The truststore path                                                                          | bot-config.json          |
| truststorePassword                       | The truststore password                                                                      | bot-config.json          |
| podProxyURL                              | The pod proxy URL                                                                            | bot-config.json          |
| podProxyUsername                         | The pod proxy username                                                                       | bot-config.json          |
| podProxyPassword                         | The pod proxy password                                                                       | bot-config.json          |
| proxyURL                                 | The agent and pod proxy URL                                                                  | bot-config.json          |
| proxyUsername                            | The agent and pod proxy username                                                             | bot-config.json          |
| proxyPassword                            | The agent and pod proxy password                                                             | bot-config.json          |
| keyManagerProxyURL                       | The key manager proxy URL                                                                    | bot-config.json          |
| keyManagerProxyUsername                  | The key manager proxy username                                                               | bot-config.json          |
| keyManagerProxyPassword                  | The key manager proxy password                                                               | bot-config.json          |
| server.port                              | Port to be used by application (e.g. 8080)                                                   | application-dev.yaml     |
| server.servlet.context-path              | Application context path (e.g. /botapp)                                                      | application-dev.yaml     |
| certs                                    | Path to the directory containing bot private key                                             | application-dev.yaml     |
| logging.file                             | The log file path (including file name)                                                      | application-dev.yaml     |
| logging.level.&lt;package&gt;            | The log level for the given package (e.g. DEBUG, INFO, WARN, ERROR)                          | application-dev.yaml     |
| access-control.name                      | The username for basic authentication                                                        | application-dev.yaml     |
| access-control.hashedPassword            | The salted hashed password for basic authentication                                          | application-dev.yaml     |
| access-control.salt                      | Salt used when hashing password                                                              | application-dev.yaml     |
| access-control.ipWhitelist               | The IP whitelist set                                                                         | application-dev.yaml     |
| access-control.urlMapping                | The endpoints protected by either basic authentication or IP whitelist                       | application-dev.yaml     |
| concurrency.bot.pool.core-size           | The bot concurrency pools coreSize                                                           | application-dev.yaml     |
| concurrency.bot.pool.max-size            | The bot concurrency pools max size                                                           | application-dev.yaml     |
| concurrency.bot.pool.queue-capacity      | The bot concurrency pools queue capacity                                                     | application-dev.yaml     |
| concurrency.bot.pool.thread-name-prefix  | The bot concurrency pools thread name prefix                                                 | application-dev.yaml     |
| concurrency.sse.pool.core-size           | The SSE concurrency pools coreSize                                                           | application-dev.yaml     |
| concurrency.sse.pool.max-size            | The SSE concurrency pools max size                                                           | application-dev.yaml     |
| concurrency.sse.pool.queue-capacity      | The SSE concurrency pools queue capacity (if 0 returns immediately if no thread available)   | application-dev.yaml     |
| concurrency.sse.pool.thread-name-prefix  | The SSE concurrency pools thread name prefix                                                 | application-dev.yaml     |
| concurrency.sse.subscriber.queue-capacity| Capacity of SSE subscriber queue. Defines the maximum number of concurrent publishers writing to the queue | application-dev.yaml     |
| concurrency.sse.subscriber.queue-timeout | How long a subscriber will wait for events before sending a keep-alive                       | application-dev.yaml     |
| cors.allowed-origin                      | The allowed origin domain                                                                    | application-dev.yaml     |
| cors.url-mapping                         | The endpoints which CORS support should be applied to                                        | application-dev.yaml     |
| xss.url-mapping                          | The endpoints which cross-site scripting protection should be applied to                     | application-dev.yaml     |
| request-origin.origin-header             | The HTTP header used to convey the request origin secret                                     | application-dev.yaml     |
| request-origin.url-mapping               | The endpoints for which origin header should be verified                                     | application-dev.yaml     |
| restclient.proxy.address                 | The rest client proxy address                                                                | application-dev.yaml     |
| restclient.proxy.port                    | The rest client port                                                                         | application-dev.yaml     |
| restclient.timeout                       | The rest client timeout                                                                      | application-dev.yaml     |
| throttling.limit                         | Limits the number of requests per second                                                     | application-dev.yaml     |
| throttling.mode                          | Throttling modes: ORIGIN - throttle based on IP or ENDPOINT - throttle based on target endpoint. If not specified, default to ENDPOINT | application-dev.yaml     |
| throttling.timeout                       | Maximum amount of time a request waits before a HTTP 408 is returned to client               | application-dev.yaml     |
| features.commandFeedback                 | The command feedback enablement                                                              | application-feature.yaml |
| features.transactionIdOnError            | The transaction id on error enablement                                                       | application-feature.yaml |
| features.eventUnexpectedErrorMessage     | The message for unexpected errors on events                                                  | application-feature.yaml |
| features.notificationBaseUrl             | The notification base URL                                                                    | application-feature.yaml |
| features.isPublicRoomAllowed             | The enablement for allowing bot addition to public rooms                                     | application-feature.yaml |
| features.publicRoomNotAllowedMessage     | The message when adding bots to public rooms is not allowed                                  | application-feature.yaml |
| features.publicRoomNotAllowedTemplate    | The template to be used when adding bots to public rooms is not allowed                      | application-feature.yaml |
| features.publicRoomNotAllowedTemplateMap | The parameter of the template to be used when adding bots to public rooms is not allowed     | application-feature.yaml |


