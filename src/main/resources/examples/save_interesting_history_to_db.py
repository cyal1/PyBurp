from java.sql import DriverManager
from org.sqlite import JDBC


def save_to_sqlite():
    SAVE_DOMAIN_END_WITH = ["example.com", "google.com"]
    SQLITE_DB_PATH = "/tmp/database.db"
    # sqlite3 /tmp/database.db "select * from burp_urls;"
    conn = DriverManager.getConnection("jdbc:sqlite:" + SQLITE_DB_PATH)
    conn.setAutoCommit(False)
    stmt = conn.createStatement()
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS burp_urls (url TEXT UNIQUE NOT NULL)")
    count = 0
    try:
        # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/proxy/Proxy.html#history()
        for requestResponse in history(
                lambda requestResponse: any(requestResponse.finalRequest().httpService().host().endswith(host) for host in SAVE_DOMAIN_END_WITH)):
            url = requestResponse.finalRequest().httpService().toString()
            sql = "INSERT OR IGNORE INTO burp_urls (url) VALUES ('%s')" % (url)
            affectedRows = stmt.executeUpdate(sql)
            if affectedRows > 0:
                count += affectedRows
        conn.commit()
    except Exception as e:
        conn.rollback()
        raise e
    finally:
        stmt.close()
        conn.close()

    print("Saved:", count, "records")


def save_to_file():
    # https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/proxy/Proxy.html#history()
    file = open("/tmp/output.txt", "w")

    # for requestResponse in history(lambda requestResponse: requestResponse.finalRequest().httpService().host().endswith(".example.com")):
    for requestResponse in history():
        if requestResponse.finalRequest().httpService().host().endswith(".example.com"):
            file.write(requestResponse.finalRequest().httpService().toString() + "\n")
    file.close()


print(history().size())
# Save on Clicking Run
save_to_sqlite()

