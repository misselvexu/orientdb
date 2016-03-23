/*
 *
 *  *  Copyright 2016 Orient Technologies LTD (info(at)orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientdb.com
 *
 */
package com.orientechnologies.security.auditing;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.security.OSecurityNull;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.concurrent.BlockingQueue;

/**
 * Thread that log asynchronously.
 *
 * @author Luca Garulli
 */
public class OAuditingLoggingThread extends Thread {
  public static final String             FROM_AUDITING  = "FROM_AUDITING";
  private final String                   databaseURL;
  private final BlockingQueue<ODocument> auditingQueue;
  private ODatabaseDocumentTx            db;
  private volatile boolean               running        = true;
  private volatile boolean               waitForAllLogs = true;
  private boolean                        isOpened       = false;

  public OAuditingLoggingThread(final String iDatabaseURL, final String iDatabaseName, final BlockingQueue auditingQueue) {
    super(Orient.instance().getThreadGroup(), "OrientDB Auditing Logging Thread - " + iDatabaseName);

    this.databaseURL = iDatabaseURL;
    this.auditingQueue = auditingQueue;
    setDaemon(true);
  }

  @Override
  public void run() {

    while (running || waitForAllLogs) {
      try {
        if (!running && auditingQueue.isEmpty()) {
          break;
        }

        final ODocument log = auditingQueue.take();

        if (!isOpened) {
          db = new ODatabaseDocumentTx(databaseURL);
          openDatabase();
          isOpened = true;
        }
        db.activateOnCurrentThread();
        db.save(log);

      } catch (InterruptedException e) {
        // IGNORE AND SOFTLY EXIT

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (db != null) {
      db.activateOnCurrentThread();
      db.close();
    }
  }

  public void sendShutdown(final boolean iWaitForAllLogs) {
    this.waitForAllLogs = iWaitForAllLogs;
    running = false;
    interrupt();
  }

  protected void openDatabase() {
    db.setProperty(FROM_AUDITING, true);
    db.setProperty(ODatabase.OPTIONS.SECURITY.toString(), OSecurityNull.class);
    try {
      db.open("admin", "any");
    } catch (Exception e) {
      OLogManager.instance().error(this, "Cannot open database '%s'", e, databaseURL);
    }
  }
}
