/*
 * Copyright 1998-2010 Linux.org.ru
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.org.linux.site;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PreparedMessage {
  private final Message message;
  private final User author;
  private final DeleteInfo deleteInfo;
  private final User deleteUser;
  private final String processedMessage;
  private final PreparedPoll poll;
  private final User commiter;

  private final EditInfoDTO lastEditInfo;
  private final User lastEditor;
  private final int editCount;

  public PreparedMessage(Connection db, Message message) throws UserNotFoundException, SQLException, PollNotFoundException {
    this.message = message;

    author = User.getUserCached(db, message.getUid());

    if (message.isDeleted()) {
      deleteInfo = DeleteInfo.getDeleteInfo(db, message.getId());

      if (deleteInfo!=null) {
        deleteUser = User.getUserCached(db, deleteInfo.getUserid());
      } else {
        deleteUser = null;
      }
    } else {
      deleteInfo = null;
      deleteUser = null;
    }

    if (message.isVotePoll()) {
      Poll rawPoll = Poll.getPollByTopic(db, message.getId());
      poll = new PreparedPoll(db, rawPoll);
    } else {
      poll = null;
    }

    if (message.getCommitby()!=0) {
      commiter = User.getUserCached(db, message.getCommitby());
    } else {
      commiter = null;
    }

    List<EditInfoDTO> editInfo = message.loadEditInfo(db);
    if (editInfo!=null && !editInfo.isEmpty()) {
      lastEditInfo = editInfo.get(0);
      lastEditor = User.getUserCached(db, lastEditInfo.getEditor());
      editCount = editInfo.size();
    } else {
      lastEditInfo = null;
      lastEditor = null;
      editCount = 0;
    }

    processedMessage = message.getProcessedMessage(db, true);
  }

  public Message getMessage() {
    return message;
  }

  public User getAuthor() {
    return author;
  }

  public DeleteInfo getDeleteInfo() {
    return deleteInfo;
  }

  public User getDeleteUser() {
    return deleteUser;
  }

  public String getProcessedMessage() {
    return processedMessage;
  }

  public PreparedPoll getPoll() {
    return poll;
  }

  public User getCommiter() {
    return commiter;
  }

  public EditInfoDTO getLastEditInfo() {
    return lastEditInfo;
  }

  public User getLastEditor() {
    return lastEditor;
  }

  public int getEditCount() {
    return editCount;
  }

  public int getId() {
    return message.getId();
  }
}
