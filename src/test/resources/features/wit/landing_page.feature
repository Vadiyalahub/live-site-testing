@wit
Feature: Women in Tech Series 2026 landing page
  As a visitor
  I want the event page to load correctly and let me register
  So that I can join the roundtable

  # This is a live website. These tests only read the page and check form validation.
  # They never send a real registration - RegistrationModal refuses to submit a valid form.

  Background:
    Given I open the Women in Tech landing page

  # ---------- Smoke: is the page up and usable? ----------

  @smoke
  Scenario: Page loads with the event title
    Then the page title should contain "Women in Tech Series 2026"
    And the main heading should contain "Women in Tech"

  @smoke
  Scenario: Top navigation links all point to real sections
    Then the top navigation should have these links:
      | Themes        |
      | Event         |
      | Agenda        |
      | Past Speakers |
      | Partners      |
      | FAQ           |
    And every navigation link should point to a section that exists

  # ---------- Regression: does each feature work? ----------

  @regression
  Scenario: FAQ answers expand and collapse
    Then the FAQ should have 6 questions
    When I click FAQ question 1
    Then the answer to FAQ question 1 should be shown
    When I click FAQ question 1
    Then the answer to FAQ question 1 should be hidden

  @regression @mobile
  Scenario: Mobile menu opens and closes on a phone
    Given I am using a phone-sized screen
    When I open the mobile menu
    Then the mobile menu should be visible
    When I close the mobile menu
    Then the mobile menu should be hidden

  @regression
  Scenario: Every speaker card shows the speaker's name
    Then every speaker card should show the speaker's name

  # Was BUG-007 (a speaker with no LinkedIn link). Fixed in the October redesign.
  @regression
  Scenario: Every speaker card links to the speaker's LinkedIn
    Then every speaker card should have a LinkedIn link

  # Was BUG-005 (meta description was just "Moderator & Panel"). Fixed in the October redesign.
  @regression
  Scenario: Page has a useful meta description for search engines
    Then the meta description should be at least 50 characters long

  # ---------- Blocked: can't run until BUG-008 is fixed ----------
  # The October redesign removed the "Reserve Seat" button, so the registration form can't be opened.
  # These scenarios passed before the redesign and will run again once a register button is back.

  @blocked @BUG-008
  Scenario: Reserve Seat opens the registration form
    When I open the registration form
    Then the registration form should be open
    And the cursor should be in the "wit-name" field

  @blocked @BUG-008
  Scenario: Registration form closes with the X button
    When I open the registration form
    And I close the registration form
    Then the registration form should be closed

  @blocked @BUG-008
  Scenario: Registration form closes with the Escape key
    When I open the registration form
    And I press the Escape key
    Then the registration form should be closed

  @blocked @BUG-008
  Scenario Outline: Registration form blocks invalid input (<case>)
    When I open the registration form
    And I enter name "<name>" and email "<email>"
    And I try to submit the registration
    Then the "<field>" field should show a validation error
    And the registration should not be sent

    Examples:
      | case          | name     | email          | field |
      | missing name  |          | jane@test.com  | name  |
      | missing email | Jane Doe |                | email |
      | invalid email | Jane Doe | not-an-email   | email |

  @blocked @BUG-008
  Scenario: Audience dropdown lists every option
    When I open the registration form
    Then the audience dropdown should list:
      | Manufacturing / Operations Leader     |
      | Engineering / Automation Leader       |
      | AI / Robotics / Digital Transformation |
      | Founder / CEO                         |
      | Investor / Strategic Buyer            |
      | Student / Early Career                |
      | Other                                 |

  # ---------- Known bugs: these FAIL until the site is fixed ----------
  # Each one matches a report in docs/BUG_REPORTS.md. Run them with: ./run-tests.sh known-bugs
  # When a bug is fixed, remove the @known-bug tag so the test joins the regression suite.

  @known-bug @BUG-008
  Scenario: Visitors can find a button to register
    Then there should be a visible button to register

  @known-bug @BUG-001
  Scenario: Links to other pages on the site are not broken
    Then no link to another page on the site should be broken

  # Passes until the event date, then fails if the countdown still says LIVE afterwards.
  @known-bug @BUG-002
  Scenario: Countdown stops saying LIVE after the event
    Then the countdown should not show "LIVE" after "2026-10-15"

  @known-bug @BUG-003
  Scenario: FAQ shows the current event date and length
    Then the FAQ should not mention "16 September"
    And the FAQ should not mention "75 minutes"

  @known-bug @BUG-004
  Scenario: Every image has alt text for screen readers
    Then every image should have alt text
