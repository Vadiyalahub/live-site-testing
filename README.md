# Live Site QA Testing: Women in Tech Series 2026

This is QA testing of a real, live web page: the
[Women in Tech Series 2026](https://uninetworkgroup.com/women-in-tech-series-2026/) event page.
It's a WordPress landing page with a registration popup, FAQ, speaker cards and a mobile menu.

I wanted to test something real instead of a practice site. That meant working out how to test it without
breaking anything, and it turned up **7 real bugs**, which are written up in [docs/BUG_REPORTS.md](docs/BUG_REPORTS.md).

Built with Java, Selenium, Cucumber and JUnit 5, run with Maven, and checked automatically every day on GitHub Actions.

## How I approached it

1. **Explored the page by hand first:** clicking through the menu, the popup, the FAQ, the footer links,
   and checking it at phone size.
2. **Automated the things visitors actually do.** That's 12 checks, and they all pass.
3. **Added checks across the whole page** for broken links, missing alt text, the search description and
   content that contradicts itself. Those found most of the bugs.
4. **Wrote up each bug** with steps to reproduce, and added a test for it that fails until it's fixed.

## Testing a live site without breaking it

The registration form sends real emails, so I couldn't just submit test data. The tests only read the page and
check validation. `RegistrationModal` refuses to click submit if the form is valid, so a test can never create
a real registration. Only invalid input gets submitted, and the browser blocks that before anything is sent.

## What's checked

These all pass:
- the page loads with the right title and heading
- every menu link jumps to a section that exists
- "Reserve Seat" opens the popup with the cursor already in the Name field
- the popup closes with × and with Escape
- a missing name, a missing email or an invalid email are all blocked
- the "I identify as" dropdown has all 7 options
- FAQ answers open and close
- the mobile menu works on a phone-sized screen
- every speaker photo is labelled with the speaker's name

And these fail, because they're the bugs:

| Bug | Severity |
|---|---|
| About Us, Privacy Policy and Terms links in the footer return 404 | High |
| The countdown still says "LIVE" weeks after the event ended | Medium |
| The session is 75 minutes in most places but 90 in the agenda | Medium |
| 6 images have no alt text | Low |
| The meta description is just "Moderator & Panel" | Low |
| One speaker interview URL has a typo: "womwn" | Low |
| One speaker (Leah Tate) has no LinkedIn link | Low |

Each failing test is tagged `@known-bug` so it doesn't turn the main run red. When a bug gets fixed, the test
starts passing, I remove the tag, and from then on it's part of the normal checks.

## Running it

You need Java 17+ and Maven. If Chrome isn't installed, Selenium downloads a copy automatically.

```bash
./run-tests.sh smoke --headed    # quick check, and you can watch the browser
./run-tests.sh regression        # all 12 working checks, about a minute
./run-tests.sh known-bugs        # the bug tests (these are supposed to fail)
```

The report is in `target/cucumber-reports/cucumber.html`. For the bug tests, the red message says exactly
what's wrong, e.g. `Broken links: {.../page/privacy-policy=404, ...}`, with a screenshot.

GitHub Actions runs the checks on every push and once a day, so if something on the live page breaks,
it shows up the next morning.

The test cases are listed in [docs/TEST_PLAN.md](docs/TEST_PLAN.md).

## Problems I ran into

- **The "Reserve Seat" button wasn't clickable.** It worked when I tried it by hand but failed in the test.
  The page fades sections in as you scroll, and until you scroll there the button has opacity 0, which Selenium
  treats as hidden. I fixed it by scrolling the button into view first, the same way a visitor would.
- **Clicks were landing in the wrong place.** The site uses smooth scrolling, so Selenium sometimes clicked while
  the page was still moving. I turn smooth scrolling off in the test browser.

Two things I handled up front for the same reasons:
- The page moves the cursor into the form a moment after the popup opens, so the focus test waits for it
  instead of checking instantly.
- Selenium's `getText()` returns an empty string for faded-out elements, so content checks read the element's
  `textContent` instead.

## What I'd add next

- Check the other pages the event page links to
- Run on Firefox and Safari as well as Chrome
- Screenshot comparison to catch layout changes
