/* Generated By:JJTree: Do not edit this line. OAlterSequenceStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.metadata.sequence.OSequence;
import com.orientechnologies.orient.core.sql.executor.OInternalResultSet;
import com.orientechnologies.orient.core.sql.executor.OResultInternal;
import com.orientechnologies.orient.core.sql.executor.OTodoResultSet;

import java.util.Map;

public class OAlterSequenceStatement extends ODDLStatement {
  OIdentifier name;
  OExpression start;
  OExpression increment;
  OExpression cache;

  public OAlterSequenceStatement(int id) {
    super(id);
  }

  public OAlterSequenceStatement(OrientSql p, int id) {
    super(p, id);
  }

  @Override public OTodoResultSet executeDDL(OCommandContext ctx) {

    String sequenceName = name.getStringValue();

    if (sequenceName == null) {
      throw new OCommandExecutionException("Cannot execute the command because it has not been parsed yet");
    }
    final ODatabaseDocument database = getDatabase();
    OSequence sequence = database.getMetadata().getSequenceLibrary().getSequence(sequenceName);
    if (sequence == null) {
      throw new OCommandExecutionException("Sequence not found: " + sequenceName);
    }

    OSequence.CreateParams params = new OSequence.CreateParams();

    if (start != null) {
      Object val = start.execute((OIdentifiable) null, ctx);
      if (!(val instanceof Number)) {
        throw new OCommandExecutionException("invalid start value for a sequence: " + val);
      }
      params.start = ((Number) val).longValue();
    }
    if (increment != null) {
      Object val = increment.execute((OIdentifiable) null, ctx);
      if (!(val instanceof Number)) {
        throw new OCommandExecutionException("invalid increment value for a sequence: " + val);
      }
      params.increment = ((Number) val).intValue();
    }
    if (cache != null) {
      Object val = cache.execute((OIdentifiable) null, ctx);
      if (!(val instanceof Number)) {
        throw new OCommandExecutionException("invalid cache value for a sequence: " + val);
      }
      params.cacheSize = ((Number) val).intValue();
    }

    sequence.updateParams(params);
    sequence.reset();
    sequence.save();

    OInternalResultSet result = new OInternalResultSet();
    OResultInternal item = new OResultInternal();
    item.setProperty("operation", "alter sequence");
    item.setProperty("sequenceName", sequenceName);
    item.setProperty("start", params.start);
    item.setProperty("increment", params.increment);
    item.setProperty("cacheSize", params.cacheSize);
    result.add(item);
    return result;
  }

  @Override public void toString(Map<Object, Object> params, StringBuilder builder) {
    builder.append("ALTER SEQUENCE ");
    name.toString(params, builder);

    if (start != null) {
      builder.append(" START ");
      start.toString(params, builder);
    }
    if (increment != null) {
      builder.append(" INCREMENT ");
      increment.toString(params, builder);
    }
    if (cache != null) {
      builder.append(" CACHE ");
      cache.toString(params, builder);
    }
  }

  @Override public OAlterSequenceStatement copy() {
    OAlterSequenceStatement result = new OAlterSequenceStatement(-1);
    result.name = name == null ? null : name.copy();
    result.start = start == null ? null : start.copy();
    result.increment = increment == null ? null : increment.copy();
    result.cache = cache == null ? null : cache.copy();
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    OAlterSequenceStatement that = (OAlterSequenceStatement) o;

    if (name != null ? !name.equals(that.name) : that.name != null)
      return false;
    if (start != null ? !start.equals(that.start) : that.start != null)
      return false;
    if (increment != null ? !increment.equals(that.increment) : that.increment != null)
      return false;
    if (cache != null ? !cache.equals(that.cache) : that.cache != null)
      return false;

    return true;
  }

  @Override public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (start != null ? start.hashCode() : 0);
    result = 31 * result + (increment != null ? increment.hashCode() : 0);
    result = 31 * result + (cache != null ? cache.hashCode() : 0);
    return result;
  }
}
/* JavaCC - OriginalChecksum=def62b1d04db5223307fe51873a9edd0 (do not edit this line) */
