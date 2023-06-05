package com.raxim.myscoutee.profile.generator;

import java.util.List;
import java.util.Set;

public interface IGenerator<T, U> {
    List<Set<U>> generate(T flags);
}
